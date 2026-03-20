package com.app.zooapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.zooapp.data.db.dao.AnimalDao
import com.app.zooapp.data.db.entity.AnimalEntity

/**
 * Clase abstracta que define la base de datos Room.
 * Se incluyen los converters para manejar listas.
 * Patrón Singleton para obtener la instancia.
 */
@Database(entities = [AnimalEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ZooDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao

    companion object {
        @Volatile
        private var INSTANCE: ZooDatabase? = null

        fun getInstance(context: Context): ZooDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZooDatabase::class.java,
                    "zoo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}