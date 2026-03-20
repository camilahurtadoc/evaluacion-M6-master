package com.app.zooapp.data.api

import com.app.zooapp.data.api.model.AnimalApi
import com.app.zooapp.data.api.model.AnimalDetailApi
import retrofit2.http.GET
import retrofit2.http.Path

interface ZooApiService {
    @GET("es/animales")
    suspend fun getAnimales(): List<AnimalApi>

    @GET("es/animales/{id}")
    suspend fun getAnimalDetail(@Path("id") id: Int): AnimalDetailApi
}