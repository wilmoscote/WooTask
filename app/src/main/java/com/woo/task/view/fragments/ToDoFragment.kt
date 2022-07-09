package com.woo.task.view.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.woo.task.R
import com.woo.task.databinding.FragmentToDoBinding
import com.woo.task.model.interfaces.RecyclerViewInterface
import com.woo.task.model.responses.TaskValues
import com.woo.task.view.adapters.TaskAdapter
import com.woo.task.view.ui.activity.NewTaskActivity
import com.woo.task.viewmodel.TasksViewModel
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.launch


@FragmentScoped
class ToDoFragment : Fragment(),RecyclerViewInterface {
    lateinit var data:MutableList<TaskValues>
    private val tasksViewModel: TasksViewModel by activityViewModels()
    private lateinit var binding: FragmentToDoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToDoBinding.inflate(layoutInflater)
        val view = binding.root
        lifecycleScope.launch {
            binding.titleBanner.text = getString(R.string.title_list_todo)
            tasksViewModel.todoTasks.observe(viewLifecycleOwner) {
                binding.rvToDo.layoutManager = LinearLayoutManager(view.context)
                binding.rvToDo.adapter = TaskAdapter(it, this@ToDoFragment)
                binding.rvToDo.visibility = View.VISIBLE
                binding.viewLoading.visibility = View.GONE
                binding.numTask.text = if (it.size in 1..1) getString(
                    R.string.task_count_0,
                    it.size.toString()
                ) else getString(R.string.task_count, it.size.toString())
            }
            binding.rvToDo.adapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
            binding.addTask.setOnClickListener {
            val intent = Intent(view.context, NewTaskActivity::class.java)
            intent.putExtra("state",1)
            startActivity(intent)
        }
            //Log.d("TASKDEBUG","RoomTask: ${taskDao.getAll()}")
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        tasksViewModel.onCreate()

    }

    override fun onPause() {
        super.onPause()

    }

    override fun onLongClick(position: Int) {
        //
    }

    override fun moveItem(id: Int,state:Int) {
        tasksViewModel.moveTask(id,state)
        Log.d("TASKDEBUG","MOVER $id TO $state")
    }

    override fun onClickDelete(id: Int) {
        tasksViewModel.removeTask(id)
        Log.d("TASKDEBUG","DELETE $id")
    }
}