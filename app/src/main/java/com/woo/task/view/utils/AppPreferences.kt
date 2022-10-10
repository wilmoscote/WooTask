package com.woo.task.view.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.woo.task.R
import com.woo.task.model.utils.AlarmModel


object AppPreferences{
    lateinit var shared: SharedPreferences

    fun setup(context: Context?) {
        if (context != null) {
            shared = context.getSharedPreferences(context.getString(R.string.preferences_key), Context.MODE_PRIVATE)
        }
    }

    /*var imc: Float?
        get() = Key.imc.getFloat()
        set(value) = Key.imc.setFloat(value)

    var vozPlanId: Int?
        get() = Key.vozPlanId.getInt()
        set(value) = Key.vozPlanId.setInt(value)

    var postUrl: String?
        get() = "https://ips-medigroup-20be8.web.app/"
        set(value) = Key.postUrl.setString(value)*/

    var font: Int?
        get() = Key.font.getInt()
        set(value) = Key.font.setInt(value)

    var theme: Int?
        get() = Key.theme.getInt()
        set(value) = Key.theme.setInt(value)

    var language: Int?
        get() = Key.language.getInt()
        set(value) = Key.language.setInt(value)

    var msg_token_fmt: String?
        get() = Key.msg_token_fmt.getString()
        set(value) = Key.msg_token_fmt.setString(value)

    var tutorial: String?
        get() = Key.tutorial.getString()
        set(value) = Key.tutorial.setString(value)

    var notifications: Boolean?
        get() = Key.notifications.getBoolean()
        set(value) = Key.notifications.setBoolean(value)

    var ads: Boolean?
        get() = Key.ads.getBoolean()
        set(value) = Key.ads.setBoolean(value)

    var bgColor: Int?
        get() = Key.bgColor.getInt()
        set(value) = Key.bgColor.setInt(value)

    private enum class Key {
        notifications,msg_token_fmt,theme,font,language,tutorial,bgColor,ads;
        fun getBoolean(): Boolean? = if (shared.contains(name)) shared.getBoolean(name, true) else true
        fun getFloat(): Float? = if (shared.contains(name)) shared.getFloat(name, 0.0f) else 0.0f
        fun getInt(): Int = if (shared.contains(name)) shared.getInt(name, 0) else 0
        fun getLong(): Long? = if (shared.contains(name)) shared.getLong(name, 0) else 0
        fun getString(): String? = if (shared.contains(name)) shared.getString(name, "") else ""

        fun setBoolean(value: Boolean?) = value?.let { shared.edit { putBoolean(name, value) } } ?: remove()
        fun setFloat(value: Float?) = value?.let { shared.edit { putFloat(name, value) } } ?: remove()
        fun setInt(value: Int?) = value?.let { shared.edit { putInt(name, value) } } ?: remove()
        fun setLong(value: Long?) = value?.let { shared.edit { putLong(name, value) } } ?: remove()
        fun setString(value: String?) = value?.let { shared.edit { putString(name, value) } } ?: remove()

        fun remove() = shared.edit { remove(name) }
    }

    fun getAlarms(): MutableList<AlarmModel> {
        val gson = GsonBuilder().setLenient().create()
        val jsn: String? = shared.getString("alarms", "")
        val alarmas = gson.fromJson(jsn, Array<AlarmModel>::class.java)?.toMutableList()
        return alarmas ?: mutableListOf<AlarmModel>()
    }

    fun setAlarms(alarms: MutableList<AlarmModel>){
        val editor = shared.edit()
        val g = Gson()
        val json = g.toJson(alarms)
        editor.putString("alarms", json)
        editor.apply()
    }
}