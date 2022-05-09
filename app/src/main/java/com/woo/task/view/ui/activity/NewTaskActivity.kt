package com.woo.task.view.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.woo.task.databinding.ActivityNewTaskBinding

class NewTaskActivity : AppCompatActivity() {
    lateinit var binding: ActivityNewTaskBinding
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
            Toast.makeText(this,"Nueva tarea agregada $state.", Toast.LENGTH_SHORT).show()
        }
    }
}