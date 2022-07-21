package com.woo.task.view.ui.activity

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.woo.task.R
import com.woo.task.databinding.ActivityConfigBinding
import com.woo.task.databinding.ActivityMainBinding
import com.woo.task.model.room.TaskDb
import com.woo.task.model.utils.ModalBottomSheet

class ConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigBinding
    private lateinit var auth: FirebaseAuth
    val modalBottomSheet = ModalBottomSheet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        Glide.with(applicationContext).load(auth.currentUser?.photoUrl)
            .placeholder(R.drawable.default_profile).circleCrop().into(binding.imgProfile)
        binding.nameProfile.text =
            if (!auth.currentUser?.displayName.isNullOrEmpty()) auth.currentUser?.displayName else getString(
                R.string.no_session_user
            )
        binding.emailProfile.text = auth.currentUser?.email

        binding.txtCurrentLanguage.text = resources.configuration.locale.displayName.toString()

        //Obtengo las preferencias del usuario.
        val sharedPref = this.getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE)
        //Veo que tema tiene seleccionado para reflejarlo en switch
        if (sharedPref.getInt("theme", 0) == 2) binding.switchTheme.isChecked = true

        //Evento para leer estado del switch y cambiar el tema de la app, asi mismo guardarlo en las preferencias del usuario.
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPref.edit()
            if (isChecked) {
                editor.putInt("theme", 2)
                setDefaultNightMode(MODE_NIGHT_YES)
            } else {
                editor.putInt("theme", 1)
                setDefaultNightMode(MODE_NIGHT_NO)
            }
            editor.apply()
        }
        binding.swithLanguage.setOnClickListener {
            modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}