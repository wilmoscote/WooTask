package com.woo.task.view.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.util.StatsLog.logEvent
import android.view.View
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.woo.task.R
import com.woo.task.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private val TAG = "RegisterDebug"
    val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.btnRegister.setOnClickListener {
            if(binding.txtEmail.text.toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(binding.txtEmail.text.toString()).matches()){
                binding.txtEmail.error = getString(R.string.email_error)
                binding.txtEmail.requestFocus()
            }else if(binding.txtPass.text.toString().isEmpty()){
                binding.txtPass.error = getString(R.string.pass_error)
                binding.txtPass.requestFocus()
            }else if(binding.txtConfirmPass.text.toString().isEmpty()){
                binding.txtConfirmPass.error = getString(R.string.pass_error)
                binding.txtConfirmPass.requestFocus()
            }else if(binding.txtPass.text.toString() != binding.txtConfirmPass.text.toString()){
                binding.txtConfirmPass.error = getString(R.string.error_confirm_pass)
                binding.txtConfirmPass.requestFocus()
            }else{
                binding.pgBar.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch{
                    auth.createUserWithEmailAndPassword(binding.txtEmail.text.toString(), binding.txtPass.text.toString())
                        .addOnCompleteListener { task->
                            if (task.isSuccessful) {
                                val bundle = Bundle()
                                bundle.putString(FirebaseAnalytics.Param.METHOD, "REGISTER_NORMAL")
                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
                                binding.pgBar.visibility = View.INVISIBLE
                                Log.d(TAG, "CreateUser:success")
                                //val user = auth.currentUser
                                startActivity(Intent(this@RegisterActivity,MainActivity::class.java))
                                finishAffinity()
                            }else{
                                binding.pgBar.visibility = View.INVISIBLE
                                Log.w(TAG, "CreateUser:failure", task.exception)
                                Toast.makeText(this@RegisterActivity,getString(R.string.error_register),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }

        binding.versionInfo.setOnLongClickListener {
            Toast.makeText(this, getString(R.string.love_message), Toast.LENGTH_LONG).show()
            return@setOnLongClickListener true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }
}