package com.app.zooapp

import com.app.zooapp.data.api.ZooApiService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Prueba unitaria para verificar que el endpoint de listado de animales
 * responde correctamente y que el cliente Retrofit puede parsear la respuesta.
 * Utiliza MockWebServer para simular el servidor sin necesidad de conexión real.
 */
class ApiUnitTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ZooApiService

    /**
     * Configura el entorno de prueba antes de cada test.
     * Inicializa el servidor mock y construye el cliente Retrofit apuntando a él.
     */
    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        // Se construye Retrofit con la URL base del servidor mock
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // mockWebServer.url("/") devuelve la URL local del servidor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ZooApiService::class.java)
    }

    /**
     * Libera los recursos del servidor mock después de cada test.
     */
    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    /**
     * Verifica que el endpoint getAnimales() devuelve una lista no vacía
     * cuando el servidor responde con un código 200 y un JSON válido.
     */
    @Test
    fun testGetAnimalesEndpoint() = runBlocking {
        // Respuesta simulada con código 200 y un cuerpo JSON que contiene un animal
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("[{\"id\":1,\"nombre\":\"Tigre\"}]")
        // Encolamos la respuesta; la próxima llamada al servidor la usará
        mockWebServer.enqueue(mockResponse)
        // Ejecutamos la llamada a la API (suspendida, por eso runBlocking)
        val response = apiService.getAnimales()
        // Comprobamos que la lista no está vacía (es decir, se parseó correctamente)
        assertTrue(response.isNotEmpty())
    }
}