package com.woo.task.model.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.woo.task.R
import com.woo.task.viewmodel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import java.util.*

class ModalBottomSheet : BottomSheetDialogFragment() {
    private lateinit var primaryLocale:Locale
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        try{
            primaryLocale = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                resources.configuration.locales.get(0)
            }else{
                resources.configuration.locale
            }
        }catch (e:Exception){
            Log.e("TASKSDEBUG",e.message.toString())
        }

        Log.d("SheetDebug","Current Language: $primaryLocale")
        val sharedPref = context?.getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE)
        val view = inflater.inflate(R.layout.language_sheet, container, false)
        val radioGroup = view.findViewById<RadioGroup>(R.id.languageRadioGroup)
        val language1 = view.findViewById<RadioButton>(R.id.language_0)
        val language2 = view.findViewById<RadioButton>(R.id.language_1)
        val language3 = view.findViewById<RadioButton>(R.id.language_2)

        when(resources.configuration.locale.toString()){
            "en","en_au","en_ca","en_gb","en_ie","en_in","en_sg","en_za" -> language1.isChecked = true
            "es","es_419","es_mx","es_us","es_co","es_ar","es_do","es_pr","es_ve" -> language2.isChecked = true
            "pt","pt_pt","pt_br" -> language3.isChecked = true
            else -> language2.isChecked = true
        }

        radioGroup.setOnCheckedChangeListener { _, _ ->
            val editor = sharedPref?.edit()
            Log.d("SheetDebug","LANGUAGE CHANGED!")
            if(language1.isChecked) {
                //setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                editor?.putInt("language",0)
                editor?.apply()
                setLocale("en")
                Log.d("SheetDebug","ENGLISH")
            }else if (language2.isChecked){
                //setDefaultNightMode(MODE_NIGHT_NO)
                editor?.putInt("language",1)
                editor?.apply()
                setLocale("es")
                Log.d("SheetDebug","SPANISH")
            }else if(language3.isChecked){
                //setDefaultNightMode(MODE_NIGHT_YES)
                editor?.putInt("language",2)
                editor?.apply()
                setLocale("pt")
                Log.d("SheetDebug","PORTUGUESE")
            }
            Toast.makeText(this@ModalBottomSheet.context,getString(R.string.language_changed_text),Toast.LENGTH_LONG).show()
        }
        return view
    }

    private fun setLocale(language:String){
        val resources = resources
        val metrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(language)
        resources.updateConfiguration(configuration,metrics)
        onConfigurationChanged(configuration)
    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }

}
