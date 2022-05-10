package com.woo.task.view.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.woo.task.databinding.ActivityNewTaskBinding
import com.woo.task.viewmodel.TasksViewModel

class NewTaskActivity : AppCompatActivity() {
    lateinit var binding: ActivityNewTaskBinding
    private val tasksViewModel: TasksViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val state = intent.getStringExtra("state")
//        val text = intent.getStringExtra("text")

        binding.btnCancel.setOnClickListener {
            onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            if(binding.newTask.text.isNotEmpty()){
                tasksViewModel.addTask(binding.newTask.text.toString(),state)
                onBackPressed()
            }else{
                binding.newTask.error = "Describa la tarea"
            }
        }
    }
}