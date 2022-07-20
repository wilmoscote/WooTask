package com.woo.task.view.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.type.DateTime
import com.woo.task.databinding.ActivityNewTaskBinding
import com.woo.task.model.room.Task
import com.woo.task.viewmodel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class NewTaskActivity : AppCompatActivity() {
    lateinit var binding: ActivityNewTaskBinding
    private val tasksViewModel: TasksViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val date = Date()
        auth = Firebase.auth

        val state = intent.getIntExtra("state",1)
//        val text = intent.getStringExtra("text")

        binding.btnCancel.setOnClickListener {
            onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            if (binding.newTask.text.isNotEmpty()) {
                tasksViewModel.newTask(
                    Task(
                        null,
                        binding.newTask.text.toString(),
                        binding.newTask.text.toString(),
                        null,
                        date.toString(),
                        date.toString(),
                        auth.currentUser?.uid,
                        "1",
                        state,
                        "1",
                        "yellow",
                        date.toString(),
                        date.toString(),
                        date.toString()
                    )
                )
                onBackPressed()
            } else {
                binding.newTask.error = "Describa la tarea"
                binding.newTask.requestFocus()
            }
        }
    }
}