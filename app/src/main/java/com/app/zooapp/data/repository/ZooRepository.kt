package com.app.zooapp.data.repository

import android.content.Context
import android.util.Log
import com.app.zooapp.data.api.ZooApiService
import com.app.zooapp.data.db.ZooDatabase
import com.app.zooapp.data.db.entity.AnimalEntity
import com.app.zooapp.utils.Constants.BASE_URL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Repositorio que actúa como única fuente de verdad.
 * Combina datos de la API y la base de local, priorizando el almacenamiento offline.
 */
class ZooRepository(context: Context) {
    private val database = ZooDatabase.getInstance(context)
    private val animalDao = database.animalDao()
    private val api: ZooApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ZooApiService::class.java)
    }

    // Observables desde Room
    fun getAllAnimals(): Flow<List<AnimalEntity>> = animalDao.getAllAnimals()
    fun getAnimalById(id: Int): Flow<AnimalEntity> = animalDao.getAnimalById(id)

    /**
     * Obtiene el listado de animales desde la API y lo guarda en Room.
     * Los detalles de cada animal se almacenan posteriormente al abrir la pantalla de detalle.
     */
    suspend fun fetchAndStoreAnimals() {
        try {
            val animalApi = api.getAnimales()
            val entities = animalApi.map { apiAnimal ->
                AnimalEntity(
                    id = apiAnimal.id,
                    nombre = apiAnimal.nombre,
                    especie = apiAnimal.especie,
                    habitat = apiAnimal.habitat,
                    dieta = apiAnimal.dieta,
                    imagen = apiAnimal.imagen,
                    descripcion = null,
                    estadoConservacion = null,
                    esperanzaVida = null,
                    pesoPromedio = null,
                    alturaPromedio = null,
                    datosCuriosos = null,
                    comidasFavoritas = null,
                    predadoresNaturales = null
                )
            }
            animalDao.insertAll(entities)
        } catch (e: Exception) {
            Log.e("ZooRepository", "Error al obtener animales", e)
        }
    }

    /**
     * Obtiene el detalle de un animal específico y lo fusiona con el registro existente.
     * Si el animal aún no está en la base (por algún motivo), se inserta completo.
     */
    suspend fun fetchAndStoreAnimalDetail(id: Int) {
        try {
            val detailApi = api.getAnimalDetail(id)
            // Intentamos obtener el animal actual para ver si existe
            val existing = try {
                animalDao.getAnimalById(id).first()
            } catch (e: Exception) {
                null
            }
            val entity = AnimalEntity(
                id = detailApi.id,
                nombre = detailApi.nombre,
                especie = detailApi.especie,
                habitat = detailApi.habitat,
                dieta = detailApi.dieta,
                imagen = detailApi.imagen,
                descripcion = detailApi.descripcion,
                estadoConservacion = detailApi.estadoConservacion,
                esperanzaVida = detailApi.esperanzaVida,
                pesoPromedio = detailApi.pesoPromedio,
                alturaPromedio = detailApi.alturaPromedio,
                datosCuriosos = detailApi.datosCuriosos,
                comidasFavoritas = detailApi.comidasFavoritas,
                predadoresNaturales = detailApi.predadoresNaturales
            )
            if (existing != null)
                animalDao.update(entity)
            else
                animalDao.insert(entity)
        } catch(e: Exception) {
            Log.e("ZooRepository", "Error al obtener detalle del animal $id", e)
        }
    }
}