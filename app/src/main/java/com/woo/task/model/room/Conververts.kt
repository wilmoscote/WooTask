package com.woo.task.model.room

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.reflect.Type

class Converters {
    @OptIn(ExperimentalSerializationApi::class)
    @TypeConverter
    fun fromList(value : List<String>) = Json.encodeToString(value)

    @OptIn(ExperimentalSerializationApi::class)
    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<String>>(value)
}