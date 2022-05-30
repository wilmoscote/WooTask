package com.woo.task.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.dynamic.IFragmentWrapper
import com.woo.task.R
import com.woo.task.databinding.FragmentToDoBinding
import com.woo.task.model.interfaces.RecyclerViewInterface
import com.woo.task.model.responses.TaskValues
import com.woo.task.model.room.TaskApp
import com.woo.task.model.room.TaskDao
import com.woo.task.model.room.TaskDb
import com.woo.task.view.adapters.TaskAdapter
import com.woo.task.view.ui.activity.EditActivity
import com.woo.task.view.ui.activity.NewTaskActivity
import com.woo.task.viewmodel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


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
        CoroutineScope(Dispatchers.Main).launch {
            binding.titleBanner.text = getString(R.string.title_list_todo)
            tasksViewModel.todoTasks.observe(viewLifecycleOwner) {
                    binding.rvToDo.layoutManager = LinearLayoutManager(view.context)
                    binding.rvToDo.adapter = TaskAdapter(it,this@ToDoFragment)
                    binding.rvToDo.visibility = View.VISIBLE
                    binding.viewLoading.visibility = View.GONE

                    binding.numTask.text = getString(R.string.task_count,it.size.toString())
            }
        binding.addTask.setOnClickListener {
            val intent = Intent(view.context, NewTaskActivity::class.java)
            intent.putExtra("state",1)
            startActivity(intent)
        }
            //Log.d("TASKDEBUG","RoomTask: ${taskDao.getAll()}")
        }
        return view
    }

    fun deleteItem(index:Int){
        Log.d("TaskDebug","Delete send to ViewModel")
       tasksViewModel.removeTask(index)
    }

    fun moveTo(tid:Int){
        Log.d("TaskDebug", "Move! $tid")
    }

    override fun onResume() {
        super.onResume()
        tasksViewModel.onCreate()
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