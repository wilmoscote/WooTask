package com.woo.task.view.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.woo.task.databinding.ActivityEditBinding

class EditActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = intent.getStringExtra("id")
        val text = intent.getStringExtra("text")

        binding.edit.setText(text)

        binding.btnCancel.setOnClickListener {
            onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            Toast.makeText(this,"Guardando información",Toast.LENGTH_SHORT).show()
        }
    }
}