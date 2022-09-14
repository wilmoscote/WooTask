package com.woo.task.view.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.room.Room
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.woo.task.R
import com.woo.task.model.room.TaskDb
import com.woo.task.view.utils.AppPreferences
import com.woo.task.view.utils.TypefaceUtil
import java.util.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var app : TaskDb
    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        screenSplash.setKeepOnScreenCondition{true}
        //Obtengo las preferencias del usuario.
        AppPreferences.setup(this)

        MobileAds.initialize(this)

        val typeface = when(AppPreferences.font){
            0 -> resources.getFont(R.font.nunito)
            1 -> resources.getFont(R.font.newsreader)
            2 -> resources.getFont(R.font.lora)
            3 -> resources.getFont(R.font.poppins)
            4 -> resources.getFont(R.font.roboto)
            5 -> resources.getFont(R.font.abeezee)
            6 -> resources.getFont(R.font.courgette)
            7 -> resources.getFont(R.font.handlee)
            8 -> resources.getFont(R.font.playball)
            else -> resources.getFont(R.font.nunito)
        }

        TypefaceUtil.overrideFont(applicationContext, "SERIF", typeface)

        //Base de datos.
        app = Room
            .databaseBuilder(this, TaskDb::class.java,"task")
            .build()

        //Veo cual es la preferencia de tema que el usuario tiene guardada y lo asigno a la app.
        when(AppPreferences.theme){
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        when(AppPreferences.language){
            0 -> {
                setLocale("en")
            }
            1 -> {
                setLocale("es")
            }
            2 -> {
                setLocale("pt")
            }
        }

        //Verifico si el usuario tiene una sesión activa para mandarlo al login o al main
        auth = Firebase.auth
        if (AppPreferences.tutorial!!.isNotEmpty()){
                startActivity(Intent(this,MainActivity::class.java))
                finish()
        }else{
            startActivity(Intent(this,SlideActivity::class.java))
            finish()
        }
    }

    private fun setLocale(language:String){
        val metrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(language)
        resources.updateConfiguration(configuration,metrics)
        onConfigurationChanged(configuration)
    }
}