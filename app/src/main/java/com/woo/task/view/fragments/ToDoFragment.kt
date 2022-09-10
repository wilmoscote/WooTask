package com.woo.task.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.woo.task.R
import com.woo.task.model.room.Task
import com.woo.task.databinding.FragmentToDoBinding
import com.woo.task.model.interfaces.RecyclerViewInterface
import com.woo.task.model.responses.TaskValues
import com.woo.task.view.adapters.TaskAdapter
import com.woo.task.viewmodel.TasksViewModel
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@FragmentScoped
class ToDoFragment : Fragment(), RecyclerViewInterface {
    lateinit var data: MutableList<TaskValues>
    private val tasksViewModel: TasksViewModel by activityViewModels()
    private lateinit var binding: FragmentToDoBinding
    private lateinit var auth: FirebaseAuth
    private val TAG = "TASKDEBUG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        binding = FragmentToDoBinding.inflate(layoutInflater)
        val view = binding.root
        lifecycleScope.launch {
            binding.titleBanner.text = getString(R.string.title_list_todo)
            tasksViewModel.todoTasks.observe(viewLifecycleOwner) {
                binding.rvToDo.layoutManager = LinearLayoutManager(this@ToDoFragment.requireContext())
                binding.rvToDo.adapter = TaskAdapter(it, this@ToDoFragment)
                binding.rvToDo.visibility = View.VISIBLE
                binding.viewLoading.visibility = View.GONE
                binding.numTask.text = if (it.size in 1..1) getString(
                    R.string.task_count_0,
                    it.size.toString()
                ) else getString(R.string.task_count, it.size.toString())
            }

            binding.addTask.setOnClickListener {

                val dialog = BottomSheetDialog(this@ToDoFragment.requireContext(), R.style.CustomBottomSheetDialog)

                val viewSheet = layoutInflater.inflate(R.layout.new_task_sheet, null)
                dialog.setOnShowListener {
                    val bottomSheetDialog = it as BottomSheetDialog
                    val parentLayout =
                        bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    parentLayout?.let { layout ->
                        //val behaviour = BottomSheetBehavior.from(layout)
                       // setupFullHeight(layout)
                       //behaviour.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    }
                }

                val txtAdd = viewSheet.findViewById<EditText>(R.id.newTask)
                val btnAdd = viewSheet.findViewById<Button>(R.id.btnSave)
                val btnCancel = viewSheet.findViewById<Button>(R.id.btnCancel)

                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                val date = sdf.format(Date())
                Log.d(TAG,date.toString())

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
                                date.toString(),
                                auth.currentUser?.uid,
                                "1",
                                1,
                                "1",
                                "yellow",
                                date.toString(),
                                date.toString(),
                                ""
                            )
                        )
                        dialog.dismiss()
                    }
                }

                dialog.setCancelable(false)

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

    override fun onResume() {
        super.onResume()
        // tasksViewModel.onCreate()
    }

    override fun onPause() {
        super.onPause()

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

    override fun updateTask(task: Task) {
        if (task.state == 1) tasksViewModel.updateTask(task)
    }
}