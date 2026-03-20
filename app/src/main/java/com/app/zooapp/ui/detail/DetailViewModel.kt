package com.app.zooapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.zooapp.data.db.entity.AnimalEntity
import com.app.zooapp.data.repository.ZooRepository
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: ZooRepository) : ViewModel() {
    private val _animal = MutableLiveData<AnimalEntity?>()
    val animal: LiveData<AnimalEntity?> = _animal
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadAnimal(id: Int) {
        viewModelScope.launch {
            repository.getAnimalById(id).asLiveData().observeForever { entity ->
                _animal.value = entity
            }
            refreshDetail(id)
        }
    }

    private fun refreshDetail(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.fetchAndStoreAnimalDetail(id)
            } catch (e: Exception) {
                _error.value = "Error al cargar detalles: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}