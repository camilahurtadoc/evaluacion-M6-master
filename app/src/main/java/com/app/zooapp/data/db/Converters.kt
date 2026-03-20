package com.app.zooapp.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Conversores para que Room pueda almacenar tipos no primitivos como List<String>.
 * Convierte la lista a JSON en la base de datos y viceversa.
 */
class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?) : String? {
        return value?.let {Gson().toJson(it)}
    }

    @TypeConverter
    fun toStringList(value: String?) : List<String>? {
        return value?.let {
            val type = object: TypeToken<List<String>>() {}.type
            Gson().fromJson(it, type)
        }
    }
}