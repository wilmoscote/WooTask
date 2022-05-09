package com.woo.task.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.woo.task.R
import com.woo.task.view.ui.activity.MainActivity

class AuthViewModel: ViewModel() {
    var logOk = MutableLiveData<Boolean>()
    private var auth = Firebase.auth
    val TAG = "AuthDebug"
    fun doLogin(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task->
                if (task.isSuccessful) {
                    logOk.postValue(true)
                }else{
                    logOk.postValue(false)
                }
            }
    }
}