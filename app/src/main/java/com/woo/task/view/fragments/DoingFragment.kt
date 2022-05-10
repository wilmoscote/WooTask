package com.woo.task.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.woo.task.R
import com.woo.task.databinding.FragmentDoingBinding
import com.woo.task.databinding.FragmentToDoBinding
import com.woo.task.view.adapters.TaskAdapter
import com.woo.task.view.ui.activity.NewTaskActivity
import com.woo.task.viewmodel.TasksViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DoingFragment : Fragment() {
    private val tasksViewModel: TasksViewModel by viewModels()
    private lateinit var binding: FragmentDoingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDoingBinding.inflate(layoutInflater)
        val view = binding.root
        CoroutineScope(Dispatchers.Main).launch {
            tasksViewModel.getDoingTasks("22")
            binding.titleBanner.text = getString(R.string.title_list_doing)
            tasksViewModel.doingTasks.observe(viewLifecycleOwner) {
                binding.rvToDo.layoutManager = LinearLayoutManager(view.context)
                binding.rvToDo.adapter = TaskAdapter(it)
                binding.rvToDo.visibility = View.VISIBLE
                binding.viewLoading.visibility = View.GONE

                binding.viewLoading.visibility = View.GONE

                binding.numTask.text = getString(R.string.task_count,it.size.toString())
            }
            binding.addTask.setOnClickListener {
                val intent = Intent(view.context, NewTaskActivity::class.java)
                intent.putExtra("state","22")
                startActivity(intent)
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        tasksViewModel.getDoingTasks("22")
    }
}