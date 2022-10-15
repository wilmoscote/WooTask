package com.woo.task.view.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.woo.task.R
import com.woo.task.databinding.FragmentDoingBinding
import com.woo.task.model.interfaces.RecyclerViewInterface
import com.woo.task.model.responses.TaskValues
import com.woo.task.model.room.Tag
import com.woo.task.model.room.Task
import com.woo.task.view.adapters.TaskAdapter
import com.woo.task.viewmodel.TasksViewModel
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@FragmentScoped
class DoingFragment : Fragment(), RecyclerViewInterface {
    private lateinit var binding: FragmentDoingBinding
    lateinit var todoTasks: MutableList<TaskValues>
    private val tasksViewModel: TasksViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth
    private var actualPosition = 0
    private lateinit var adapter: TaskAdapter
    private var activeFilter = ""
    lateinit var tagList: List<Tag>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        binding = FragmentDoingBinding.inflate(layoutInflater)
        val view = binding.root
        lifecycleScope.launch {

            tasksViewModel.getTags()

            tasksViewModel.tags.observe(viewLifecycleOwner){
                tagList = it
            }

            binding.titleBanner.text = getString(R.string.title_list_doing)
            tasksViewModel.doingTasks.observe(viewLifecycleOwner) {
                todoTasks = it.toMutableList()
                binding.rvToDo.layoutManager =
                    LinearLayoutManager(this@DoingFragment.requireContext())
                adapter = TaskAdapter(todoTasks, this@DoingFragment)
                binding.rvToDo.adapter = adapter
                binding.rvToDo.setItemViewCacheSize(todoTasks.size)
                binding.rvToDo.visibility = View.VISIBLE
                binding.viewLoading.visibility = View.GONE
                binding.rvToDo.scrollToPosition(actualPosition)
                binding.numTask.text = if (todoTasks.size in 1..1) getString(
                    R.string.task_count_0,
                    it.size.toString()
                ) else getString(R.string.task_count, it.size.toString())
            }
            binding.addTask.setOnClickListener {
                val dialog =
                    BottomSheetDialog(this@DoingFragment.requireContext(), R.style.CustomBottomSheetDialog)

                val viewSheet = layoutInflater.inflate(R.layout.new_task_sheet, null)
                dialog.setOnShowListener {
                    val bottomSheetDialog = it as BottomSheetDialog
                    val parentLayout =
                        bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    parentLayout?.let { layout ->
                        //val behaviour = BottomSheetBehavior.from(layout)
                        //setupFullHeight(layout)
                        // behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }

                val txtAdd = viewSheet.findViewById<EditText>(R.id.newTask)
                val btnAdd = viewSheet.findViewById<Button>(R.id.btnSave)
                val btnCancel = viewSheet.findViewById<Button>(R.id.btnCancel)

                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                val date = sdf.format(Date())

                btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

                btnAdd.setOnClickListener {
                    if (txtAdd.text.trim().isEmpty()) {
                        txtAdd.requestFocus()
                        Toast.makeText(
                            context,
                            getString(R.string.new_task_hint),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        tasksViewModel.newTask(
                            Task(
                                null,
                                txtAdd.text.toString(),
                                txtAdd.text.toString(),
                                null,
                                date.toString(),
                                "",
                                auth.currentUser?.uid,
                                "1",
                                2,
                                "1",
                                "yellow",
                                date.toString(),
                                date.toString(),
                                "",
                                listOf<String>()
                            )
                        )
                        dialog.dismiss()
                    }
                }

                dialog.setCancelable(false)

                dialog.setContentView(viewSheet)

                dialog.show()
            }
            TooltipCompat.setTooltipText(binding.btnFilter, getString(R.string.filter_text))
            binding.btnFilter.setOnClickListener {
                val tags = getTags()

                val dialog = BottomSheetDialog(
                    this@DoingFragment.requireContext(),
                    R.style.CustomBottomSheetDialog
                )
                val viewSheet = LayoutInflater.from(context).inflate(R.layout.filter_sheet, null)
                dialog.setOnShowListener {
                    val bottomSheetDialog = it as BottomSheetDialog
                    val parentLayout =
                        bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    parentLayout?.let { _ ->
                        //setupFullHeight(layout)
                    }
                }

                val tagGroup = viewSheet.findViewById<ChipGroup>(R.id.tagGroup)
                val notTagGeneral = viewSheet.findViewById<TextView>(R.id.noTagsGeneral)

                if (tags.isNotEmpty()) {
                    for (tag in tags) {
                        val chip = Chip(context)
                        chip.text = tag.tagName
                        //chip.isCloseIconVisible = true
                        chip.isCheckable = true
                        chip.isClickable = true
                        if(activeFilter != "0" && chip.text == activeFilter) {
                            chip.isChecked = true
                            Log.d("TASKDEBUG", "checking chip active")
                        }
                        /*chip.setOnCloseIconClickListener {
                            MaterialAlertDialogBuilder(this@ToDoFragment.requireContext())
                                .setTitle(context?.getString(R.string.delete_tag_title))
                                .setMessage(context?.getString(R.string.delete_tag_message))
                                .setPositiveButton(context?.getString(R.string.dialog_confirm)) { _, _ ->
                                    tagGroup.removeView(chip)
                                    tag.id?.let { id -> removeTag(id) }
                                    if (tagGroup.size <= 0) notTagGeneral.isVisible = true
                                }
                                .setNegativeButton(context?.getString(R.string.dialog_cancel)) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }*/
                        tagGroup.addView(chip)
                    }
                    tagGroup.isVisible = true
                } else {
                    notTagGeneral.isVisible = true
                }

                tagGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                    if (tagGroup.checkedChipIds.size > 0) {
                        val chipSelected = viewSheet.findViewById<Chip>(tagGroup.checkedChipId).text.toString()
                        activeFilter = chipSelected
                        Log.d("TASKDEBUG", "TAG CHECKED! $chipSelected ID: ${tagGroup.checkedChipId} - ACTIVE: $activeFilter")
                        // updateTags(task, tagsSelected)
                        val taskFiltered =
                            todoTasks.filter { etiqueta -> etiqueta.tags.contains(chipSelected) }
                        binding.rvToDo.layoutManager = LinearLayoutManager(this@DoingFragment.requireContext())
                        adapter = TaskAdapter(taskFiltered, this@DoingFragment)
                        binding.rvToDo.adapter = adapter
                        binding.rvToDo.setItemViewCacheSize(taskFiltered.size)
                        binding.numTask.text = if (taskFiltered.size in 1..1) getString(
                            R.string.task_count_0,
                            taskFiltered.size.toString()
                        ) else getString(R.string.task_count, taskFiltered.size.toString())
                        adapter.notifyDataSetChanged()
                    } else {
                        binding.rvToDo.layoutManager = LinearLayoutManager(this@DoingFragment.requireContext())
                        adapter = TaskAdapter(todoTasks, this@DoingFragment)
                        binding.rvToDo.adapter = adapter
                        binding.rvToDo.setItemViewCacheSize(todoTasks.size)
                        binding.numTask.text = if (todoTasks.size in 1..1) getString(
                            R.string.task_count_0,
                            todoTasks.size.toString()
                        ) else getString(R.string.task_count, todoTasks.size.toString())
                        adapter.notifyDataSetChanged()
                        activeFilter = ""
                    }
                }

                dialog.setCancelable(true)
                dialog.setContentView(viewSheet)
                dialog.show()
            }
        }
        return view
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    fun deleteItem(id: Int) {
        Log.d("TaskDebug", "Delete send to ViewModel")
//        tasksViewModel.removeTask(id)
    }

    override fun onLongClick(position: Int) {
        //
    }

    override fun moveItem(id: Int?, state: Int) {
        tasksViewModel.moveTask(id ?: 0, state)
        Log.d("TASKDEBUG", "MOVER $id TO $state")
    }

    override fun onClickDelete(id: Int, state: Int) {
        tasksViewModel.removeTask(id, state)
        Log.d("TASKDEBUG", "DELETE $id")
    }

    override fun updateTask(task: Task, position:Int) {
        if (task.state == 2) tasksViewModel.updateTask(task)
        actualPosition = position
    }

    override fun addTag(tag: String) {
        //
    }

    override fun removeTag(id: Int) {
        //
    }

    override fun getTags(): List<Tag> {
        return tagList
    }
}