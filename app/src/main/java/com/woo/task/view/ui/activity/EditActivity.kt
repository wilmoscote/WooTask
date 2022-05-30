package com.woo.task.view.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.woo.task.databinding.ActivityEditBinding
import com.woo.task.model.room.Task
import com.woo.task.model.room.TaskDao
import com.woo.task.viewmodel.TasksViewModel
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.internal.lifecycle.HiltViewModelMap
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
class EditActivity: AppCompatActivity() {
    lateinit var binding: ActivityEditBinding
    val taskViewModel by viewModels<TasksViewModel>()
    private lateinit var task:Task
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = intent.getStringExtra("id")
        val text = intent.getStringExtra("text")
        lifecycleScope.launch {
            taskViewModel.getTaskById(id?.toInt())
        }

        taskViewModel.editTask.observe(this){
            task = it
            Log.d("TASKDEBUG","Get Task ${it.toString()}!")
        }
        binding.edit.setText(text)

        binding.btnCancel.setOnClickListener {
            onBackPressed()
        }


        binding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                Log.d("TASKDEBUG","EDITAR!")
                task.text = binding.edit.text.toString()
                task.title = binding.edit.text.toString()
                taskViewModel.updateTask(task)
            }
            onBackPressed()
        }
    }
}