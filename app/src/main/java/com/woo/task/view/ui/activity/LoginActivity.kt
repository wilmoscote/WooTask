package com.woo.task.view.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import com.woo.task.R
import com.woo.task.databinding.ActivityLoginBinding
import com.woo.task.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            if(binding.txtEmail.text.toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(binding.txtEmail.text.toString()).matches()){
                binding.txtEmail.error = getString(R.string.email_error)
                binding.txtEmail.requestFocus()
            }else if(binding.txtPass.text.toString().isEmpty()){
                binding.txtPass.error = getString(R.string.pass_error)
                binding.txtPass.requestFocus()
            }else{
                authViewModel.doLogin(binding.txtEmail.text.toString(),binding.txtPass.text.toString())
            }
        }

        authViewModel.logOk.observe(this){
            if (it){
                Toast.makeText(this,"OK",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,"FAIL",Toast.LENGTH_LONG).show()
            }
        }

        binding.btnNologin.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        binding.versionInfo.setOnLongClickListener {
            Toast.makeText(this,getString(R.string.love_message),Toast.LENGTH_LONG).show()


            return@setOnLongClickListener true
        }
    }
}