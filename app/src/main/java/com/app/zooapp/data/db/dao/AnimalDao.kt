package com.app.zooapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.zooapp.data.db.entity.AnimalEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la entidad Animal.
 * Define las operaciones de base de datos, todas suspend (excepto las queries con Flow).
 */
@Dao
interface AnimalDao {
    // Flow permite observar cambios en la base de datos automáticamente
    @Query("SELECT * FROM animales ORDER BY nombre")
    fun getAllAnimals(): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animales WHERE id = :id")
    fun getAnimalById(id: Int): Flow<AnimalEntity>

    // En caso de conflicto por id duplicado, se reemplaza (OnConflictStrategy.REPLACE)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(animals: List<AnimalEntity>): List<Long>  // ← retorna IDs

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(animal: AnimalEntity): Long  // ← retorna el ID insertado

    @Update
    suspend fun update(animal: AnimalEntity): Int  // ← retorna número de filas afectadas
}