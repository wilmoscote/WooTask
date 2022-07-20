package com.woo.task.view.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.woo.task.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Obtengo las preferencias del usuario.
        val sharedPref = this.getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE)
        //Veo cual es la preferencia de tema que el usuario tiene guardada y lo asigno a la app.
        when(sharedPref.getInt("theme",0)){
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        //Verifico si el usuario tiene una sesión activa para mandarlo al login o al main
        auth = Firebase.auth
        if (auth.currentUser != null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }else{
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }
}