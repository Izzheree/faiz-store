package com.faiz0033.faizstore.presentation.add_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.faiz0033.faizstore.domain.model.Laptop
import com.faiz0033.faizstore.domain.repository.ImageUploadRepository
import com.faiz0033.faizstore.domain.repository.LaptopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

class AddEditLaptopViewModel(
    private val laptopRepository: LaptopRepository,
    private val imageUploadRepository: ImageUploadRepository,
    private val ownerEmail: String,
    private val laptopId: String? = null
) : ViewModel() {

    var name = MutableStateFlow("")
    var brand = MutableStateFlow("")
    var category = MutableStateFlow("")
    var processor = MutableStateFlow("")
    var ram = MutableStateFlow("")
    var storage = MutableStateFlow("")
    var price = MutableStateFlow("")
    var stock = MutableStateFlow("")
    var description = MutableStateFlow("")
    
    // Existing image URL from edit mode
    private var existingImageUrl: String? = null
    
    // Selected image bytes ready for upload
    private var selectedImageBytes: ByteArray? = null

    // Local image path for offline display fallback
    private var localImagePath: String? = null

    fun setLocalImagePath(path: String) {
        localImagePath = path
    }

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()
    
    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    init {
        if (laptopId != null) {
            loadLaptop(laptopId)
        }
    }

    private fun loadLaptop(id: String) {
        viewModelScope.launch {
            laptopRepository.getLaptopById(id).firstOrNull()?.let { laptop ->
                name.value = laptop.name
                brand.value = laptop.brand
                category.value = laptop.category ?: ""
                processor.value = laptop.processor ?: ""
                ram.value = laptop.ram ?: ""
                storage.value = laptop.storage ?: ""
                price.value = laptop.price.toString()
                stock.value = laptop.stock?.toString() ?: ""
                description.value = laptop.description ?: ""
                existingImageUrl = laptop.imageUrl
            }
        }
    }

    fun setImageBytes(bytes: ByteArray) {
        selectedImageBytes = bytes
    }

    fun clearError() {
        _error.value = null
    }

    fun saveLaptop() {
        val currentName = name.value.trim()
        val currentBrand = brand.value.trim()
        val currentPrice = price.value.toDoubleOrNull()
        
        if (currentName.isEmpty() || currentBrand.isEmpty() || currentPrice == null) {
            _error.value = "Name, Brand, and valid Price are required."
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            
            var finalImageUrl = existingImageUrl

            // 1. Upload Image if a new one was selected
            if (selectedImageBytes != null) {
                _isUploadingImage.value = true
                val result = imageUploadRepository.uploadImage(selectedImageBytes!!, "laptop_image.jpg")
                _isUploadingImage.value = false
                
                result.fold(
                    onSuccess = { url -> 
                        finalImageUrl = url 
                    },
                    onFailure = { e ->
                        // Fallback to local image path if upload fails (e.g., offline)
                        if (localImagePath != null) {
                            finalImageUrl = "file://$localImagePath"
                        } else {
                            _error.value = "Image upload failed: ${e.message}"
                            _isSaving.value = false
                            return@launch
                        }
                    }
                )
            } else if (localImagePath != null) {
                finalImageUrl = "file://$localImagePath"
            }

            // 2. Save Laptop (offline-first: saves to Room first, then tries API)
            val laptop = Laptop(
                id = laptopId ?: UUID.randomUUID().toString(),
                name = currentName,
                brand = currentBrand,
                price = currentPrice,
                imageUrl = finalImageUrl,
                description = description.value.trim().takeIf { it.isNotEmpty() },
                processor = processor.value.trim().takeIf { it.isNotEmpty() },
                ram = ram.value.trim().takeIf { it.isNotEmpty() },
                storage = storage.value.trim().takeIf { it.isNotEmpty() },
                category = category.value.trim().takeIf { it.isNotEmpty() },
                stock = stock.value.toIntOrNull(),
                ownerEmail = ownerEmail
            )

            try {
                if (laptopId != null) {
                    laptopRepository.updateLaptop(laptop)
                } else {
                    laptopRepository.addLaptop(laptop)
                }
                _saveSuccess.value = true
            } catch (e: Exception) {
                _error.value = "Failed to save laptop: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }

    class Factory(
        private val laptopRepository: LaptopRepository,
        private val imageUploadRepository: ImageUploadRepository,
        private val ownerEmail: String,
        private val laptopId: String? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddEditLaptopViewModel::class.java)) {
                return AddEditLaptopViewModel(laptopRepository, imageUploadRepository, ownerEmail, laptopId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
