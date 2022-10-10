package com.woo.task.view.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.woo.task.R
import com.woo.task.model.room.TaskApp
import com.woo.task.view.utils.AppPreferences
import com.woo.task.view.utils.TypefaceUtil
import java.util.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        screenSplash.setKeepOnScreenCondition{true}
        //Obtengo las preferencias del usuario.
        AppPreferences.setup(this)
        MobileAds.initialize(this)
        try{
            val typeface = when(AppPreferences.font){
                0 -> ResourcesCompat.getFont(this,R.font.nunito)
                1 -> ResourcesCompat.getFont(this,R.font.newsreader)
                2 -> ResourcesCompat.getFont(this,R.font.lora)
                3 -> ResourcesCompat.getFont(this,R.font.poppins)
                4 -> ResourcesCompat.getFont(this,R.font.roboto)
                5 -> ResourcesCompat.getFont(this,R.font.abeezee)
                6 -> ResourcesCompat.getFont(this,R.font.courgette)
                7 -> ResourcesCompat.getFont(this,R.font.handlee)
                8 -> ResourcesCompat.getFont(this,R.font.playball)
                else -> ResourcesCompat.getFont(this,R.font.nunito)
            }
            TypefaceUtil.overrideFont(applicationContext, "SERIF", typeface!!)
        }catch (e:Exception){
            Log.e("TASKDEBUG",e.message.toString())
        }

        //Base de datos.
        TaskApp.provideRoom(this)

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