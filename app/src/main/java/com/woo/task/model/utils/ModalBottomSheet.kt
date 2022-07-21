package com.woo.task.model.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.woo.task.R
import java.util.*

class ModalBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        val primaryLocale: Locale = context?.resources?.configuration?.locales?.get(0) ?: resources.configuration.locale
        Log.d("SheetDebug","Current Language: $primaryLocale")
        val sharedPref = context?.getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE)
        val view = inflater.inflate(R.layout.language_sheet, container, false)
        val radioGroup = view.findViewById<RadioGroup>(R.id.languageRadioGroup)
        val language1 = view.findViewById<RadioButton>(R.id.language_0)
        val language2 = view.findViewById<RadioButton>(R.id.language_1)
        val language3 = view.findViewById<RadioButton>(R.id.language_2)

        when(resources.configuration.locale.toString()){
            "en" -> language1.isChecked = true
            "es" -> language2.isChecked = true
            "pt" -> language3.isChecked = true
            else -> language2.isChecked = true
        }

        radioGroup.setOnCheckedChangeListener { _, _ ->
            val editor = sharedPref?.edit()
            Log.d("SheetDebug","LANGUAGE CHANGED!")
            if(language1.isChecked) {
                //setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                editor?.putInt("language",0)
                setLocale("en")
                Log.d("SheetDebug","ENGLISH")
            }else if (language2.isChecked){
                //setDefaultNightMode(MODE_NIGHT_NO)
                editor?.putInt("language",1)
                setLocale("es")
                Log.d("SheetDebug","SPANISH")
            }else if(language3.isChecked){
                //setDefaultNightMode(MODE_NIGHT_YES)
                editor?.putInt("language",2)
                setLocale("pt")
                Log.d("SheetDebug","PORTUGUESE")
            }
            editor?.apply()
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
