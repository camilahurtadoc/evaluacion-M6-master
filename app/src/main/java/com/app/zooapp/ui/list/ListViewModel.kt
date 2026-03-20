package com.app.zooapp.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.zooapp.data.db.entity.AnimalEntity
import com.app.zooapp.data.repository.ZooRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de listado.
 * Expone la lista de animales desde Room (LiveData) y maneja la recarga desde la API.
 */
class ListViewModel(private val repository: ZooRepository) : ViewModel() {

    // LiveData vinculada a Flow de Room: se actualiza automáticamente cuando cambia la BD
    val animals: LiveData<List<AnimalEntity>> = repository.getAllAnimals().asLiveData()
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * Refresca los datos desde la red.
     * Durante la operación, isLoading se pone a true y se limpia el error.
     * Si ocurre una excepción, se publica en _error.
     */
    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.fetchAndStoreAnimals()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al cargar los datos"
            } finally {
                _isLoading.value = false
            }
        }
    }
}