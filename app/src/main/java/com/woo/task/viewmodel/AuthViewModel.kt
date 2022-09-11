package com.woo.task.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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