package com.woo.task.view.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.woo.task.databinding.ActivityPoliciesBinding

class PoliciesActivity : AppCompatActivity() {
    lateinit var binding: ActivityPoliciesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPoliciesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}