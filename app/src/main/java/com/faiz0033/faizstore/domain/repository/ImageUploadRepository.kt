package com.faiz0033.faizstore.domain.repository

interface ImageUploadRepository {
    suspend fun uploadImage(imageBytes: ByteArray, fileName: String = "image.jpg"): Result<String>
}
