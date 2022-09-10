package com.woo.task.view.utils

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import java.lang.reflect.Field


object TypefaceUtil {
    fun overrideFont(
        context: Context,
        defaultFontNameToOverride: String,
        customFontTypeface: Typeface
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val newMap: MutableMap<String, Typeface> = HashMap()
            newMap["serif"] = customFontTypeface
            try {
                val staticField: Field = Typeface::class.java
                    .getDeclaredField("sSystemFontMap")
                staticField.isAccessible = true
                staticField.set(null, newMap)
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        } else {
            try {
                val defaultFontTypefaceField: Field =
                    Typeface::class.java.getDeclaredField(defaultFontNameToOverride)
                defaultFontTypefaceField.isAccessible = true
                defaultFontTypefaceField.set(null, customFontTypeface)
            } catch (e: Exception) {
                Log.e(
                    TypefaceUtil::class.java.simpleName,
                    "Can not set custom font jaja instead of $defaultFontNameToOverride"
                )
            }
        }
    }
}