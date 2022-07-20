package com.woo.task.view.ui.activity

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.*
import com.woo.task.R
import com.woo.task.databinding.ActivityConfigBinding
import com.woo.task.databinding.ActivityMainBinding

class ConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Obtengo las preferencias del usuario.
        val sharedPref = this.getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE)
        //Veo que tema tiene seleccionado para reflejarlo en el radio group.
        when(sharedPref.getInt("theme",0)){
            0 -> binding.radioButton1.isChecked = true
            1 -> binding.radioButton2.isChecked = true
            2 -> binding.radioButton3.isChecked = true
        }

        //Evento para leer a cual radio button selecciona y cambiar el tema de la app, asi mismo guardarlo en las preferencias del usuario.
        binding.themeRadioGroup.setOnCheckedChangeListener { _, _ ->
            val editor = sharedPref.edit()
            if(binding.radioButton1.isChecked) {
                setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                editor.putInt("theme",0)
            }else if (binding.radioButton2.isChecked){
                setDefaultNightMode(MODE_NIGHT_NO)
                editor.putInt("theme",1)
            }else if(binding.radioButton3.isChecked){
                setDefaultNightMode(MODE_NIGHT_YES)
                editor.putInt("theme",2)
            }
            editor.apply()
        }
    }
}