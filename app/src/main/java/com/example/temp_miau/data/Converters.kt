package com.example.temp_miau.data

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromList(value: List<String>): String {
        return Json.encodeToString(
            ListSerializer(String.serializer()),
            value
        )
    }

    @TypeConverter
    fun toList(value: String): List<String> {
        return Json.decodeFromString(
            ListSerializer(String.serializer()),
            value
        )
    }
}