package com.woo.task.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.woo.task.R
import com.woo.task.databinding.FragmentDoneBinding
import com.woo.task.model.interfaces.RecyclerViewInterface
import com.woo.task.view.adapters.TaskAdapter
import com.woo.task.view.ui.activity.NewTaskActivity
import com.woo.task.viewmodel.TasksViewModel
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@FragmentScoped
class DoneFragment : Fragment(),RecyclerViewInterface {

    private lateinit var binding: FragmentDoneBinding
    private val tasksViewModel: TasksViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDoneBinding.inflate(layoutInflater)
        val view = binding.root
        lifecycleScope.launch {
            binding.titleBanner.text = getString(R.string.title_list_done)
            tasksViewModel.doneTasks.observe(viewLifecycleOwner) {
                binding.rvToDo.layoutManager = LinearLayoutManager(view.context)
                binding.rvToDo.adapter = TaskAdapter(it,this@DoneFragment)
                binding.rvToDo.visibility = View.VISIBLE
                binding.viewLoading.visibility = View.GONE
                binding.viewLoading.visibility = View.GONE

                binding.numTask.text = if(it.size in 1..1) getString(R.string.task_count_0,it.size.toString()) else getString(R.string.task_count,it.size.toString())
            }
            binding.addTask.setOnClickListener {
                val intent = Intent(view.context, NewTaskActivity::class.java)
                intent.putExtra("state",3)
                startActivity(intent)
            }
        }
        return view
    }

    fun deleteItem(id:Int){
        Log.d("TaskDebug","Delete send to ViewModel")
        tasksViewModel.removeTask(id)
    }

    override fun onResume() {
        super.onResume()
        //tasksViewModel.getDoneTasks("23")
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