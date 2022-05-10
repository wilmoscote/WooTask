package com.woo.task.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.woo.task.R
import com.woo.task.databinding.FragmentToDoBinding
import com.woo.task.view.adapters.TaskAdapter
import com.woo.task.view.ui.activity.EditActivity
import com.woo.task.view.ui.activity.NewTaskActivity
import com.woo.task.viewmodel.TasksViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoFragment : Fragment() {

    private val tasksViewModel: TasksViewModel by viewModels()
    private lateinit var binding: FragmentToDoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToDoBinding.inflate(layoutInflater)
        val view = binding.root
        CoroutineScope(Dispatchers.Main).launch {
            tasksViewModel.getTodoTasks(21)
            binding.titleBanner.text = getString(R.string.title_list_todo)
            tasksViewModel.todoTasks.observe(viewLifecycleOwner) {
                    binding.rvToDo.layoutManager = LinearLayoutManager(view.context)
                    binding.rvToDo.adapter = TaskAdapter(it)
                    binding.rvToDo.visibility = View.VISIBLE
                    binding.viewLoading.visibility = View.GONE

                    binding.numTask.text = getString(R.string.task_count,it.size.toString())
                    Log.d("TASKDEBUG", "SNAP!")
            }
        binding.addTask.setOnClickListener {
            val intent = Intent(view.context, NewTaskActivity::class.java)
            intent.putExtra("state","21")
            startActivity(intent)
        }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        tasksViewModel.getTodoTasks(21)
    }
}