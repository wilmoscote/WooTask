package com.woo.task.view.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.woo.task.BuildConfig
import com.woo.task.R
import com.woo.task.databinding.ActivityConfigBinding
import com.woo.task.model.utils.ModalBottomSheet
import com.woo.task.view.utils.AppPreferences
import yuku.ambilwarna.AmbilWarnaDialog

class ConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigBinding
    private lateinit var auth: FirebaseAuth
    private val modalBottomSheet = ModalBottomSheet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Obtengo las preferencias del usuario.
        AppPreferences.setup(this)

        auth = Firebase.auth
        Glide.with(applicationContext).load(auth.currentUser?.photoUrl)
            .placeholder(R.drawable.default_profile).circleCrop().into(binding.imgProfile)
        binding.nameProfile.text =
            if (!auth.currentUser?.displayName.isNullOrEmpty()) auth.currentUser?.displayName else getString(
                R.string.no_session_user
            )
        binding.emailProfile.text = auth.currentUser?.email
        binding.txtVersion.text = getString(R.string.version_info) + BuildConfig.VERSION_NAME
        binding.txtCurrentLanguage.text = resources.configuration.locale.displayName.toString()
        binding.txtCurrentColor.setBackgroundColor(if(AppPreferences.bgColor!! != 0) AppPreferences.bgColor!! else Color.WHITE)

        //Veo que tema tiene seleccionado para reflejarlo en switch
        if (AppPreferences.theme == 2) binding.switchTheme.isChecked = true

        //Evento para leer estado del switch y cambiar el tema de la app, asi mismo guardarlo en las preferencias del usuario.
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppPreferences.theme = 2
                setDefaultNightMode(MODE_NIGHT_YES)
            } else {
                AppPreferences.theme = 1
                setDefaultNightMode(MODE_NIGHT_NO)
            }
        }

        binding.txtCurrentFont.text = when(AppPreferences.font){
            0 -> "Nunito"
            1 -> "Newsreader"
            2 -> "Lora"
            3 -> "Poppins"
            4 -> "Roboto"
            5 -> "Abeezee"
            6 -> "Courguette"
            7 -> "Handlee"
            8 -> "Playball"
            else -> "Nunito"
        }

        binding.swithLanguage.setOnClickListener {
            modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)
        }

        binding.switchFont.setOnClickListener {
            val dialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
            val viewSheet = LayoutInflater.from(this).inflate(R.layout.font_sheet, null)
            dialog.setOnShowListener {
                val bottomSheetDialog = it as BottomSheetDialog
                val parentLayout =
                    bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                parentLayout?.let { layout ->
                    //setupFullHeight(layout)
                }
            }
            val radioGroup = viewSheet.findViewById<RadioGroup>(R.id.fontRadioGroup)
            val font0 = viewSheet.findViewById<RadioButton>(R.id.font_0)
            val font1 = viewSheet.findViewById<RadioButton>(R.id.font_1)
            val font2 = viewSheet.findViewById<RadioButton>(R.id.font_2)
            val font3 = viewSheet.findViewById<RadioButton>(R.id.font_3)
            val font4 = viewSheet.findViewById<RadioButton>(R.id.font_4)
            val font5 = viewSheet.findViewById<RadioButton>(R.id.font_5)
            val font6 = viewSheet.findViewById<RadioButton>(R.id.font_6)
            val font7 = viewSheet.findViewById<RadioButton>(R.id.font_7)
            val font8 = viewSheet.findViewById<RadioButton>(R.id.font_8)

            when(AppPreferences.font){
                0 -> font0.isChecked = true
                1 -> font1.isChecked = true
                2 -> font2.isChecked = true
                3 -> font3.isChecked = true
                4 -> font4.isChecked = true
                5 -> font5.isChecked = true
                6 -> font6.isChecked = true
                7 -> font7.isChecked = true
                8 -> font8.isChecked = true
                else -> font0.isChecked = true
            }

            radioGroup.setOnCheckedChangeListener { _, _ ->
                if(font0.isChecked) {
                    //setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                    AppPreferences.font = 0
                }else if (font1.isChecked){
                    //setDefaultNightMode(MODE_NIGHT_NO)
                    AppPreferences.font = 1
                }else if(font2.isChecked){
                    //setDefaultNightMode(MODE_NIGHT_YES)
                    AppPreferences.font = 2
                }else if(font3.isChecked){
                    //setDefaultNightMode(MODE_NIGHT_YES)
                    AppPreferences.font = 3
                }else if(font4.isChecked){
                    //setDefaultNightMode(MODE_NIGHT_YES)
                    AppPreferences.font = 4
                }else if(font5.isChecked){
                    //setDefaultNightMode(MODE_NIGHT_YES)
                    AppPreferences.font = 5
                }else if(font6.isChecked){
                    //setDefaultNightMode(MODE_NIGHT_YES)
                    AppPreferences.font = 6
                }else if(font7.isChecked){
                    //setDefaultNightMode(MODE_NIGHT_YES)
                    AppPreferences.font = 7
                }else if(font8.isChecked){
                    //setDefaultNightMode(MODE_NIGHT_YES)
                    AppPreferences.font = 8
                }
                try{
                    Toast.makeText(this,getString(R.string.language_changed_text),
                        Toast.LENGTH_LONG).show()
                }catch (e:Exception){
                    //Log.e("TASKDEBUG",e.message.toString())
                }

            }
            dialog.setCancelable(true)
            dialog.setContentView(viewSheet)
            dialog.show()
        }

        binding.switchNotifications.isChecked = AppPreferences.notifications!!

        binding.switchNotifications.setOnCheckedChangeListener { _, b ->
            AppPreferences.notifications = b
        }

        binding.layoutLogout.setOnClickListener {
            MaterialAlertDialogBuilder(this@ConfigActivity)
                .setTitle(resources.getString(R.string.logout_title))
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(resources.getString(R.string.dialog_confirm)){_,_->
                    Firebase.auth.signOut()
                    startActivity(Intent(this@ConfigActivity,LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton(resources.getString(R.string.dialog_cancel)){dialog,_->
                    dialog.dismiss()
                }
                .show()
        }

        binding.layoutPolicy.setOnClickListener {
            startActivity(Intent(this, PoliciesActivity::class.java))
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.switchBackColor.setOnClickListener {
            val dialog = AmbilWarnaDialog(this, if(AppPreferences.bgColor!! != 0) AppPreferences.bgColor!! else Color.WHITE,
                object : AmbilWarnaDialog.OnAmbilWarnaListener {
                    override fun onCancel(dialog: AmbilWarnaDialog?) {

                    }

                    override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                        binding.txtCurrentColor.setBackgroundColor(color)
                        AppPreferences.bgColor = color
                        //Log.d("ColorDebug","ColorS Selected: ${AppPreferences.bgColor!!}")
                    }

                })
            dialog.show()
        }
    }
}