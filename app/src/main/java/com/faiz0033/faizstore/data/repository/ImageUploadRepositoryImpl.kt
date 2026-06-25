package com.faiz0033.faizstore.data.repository

import com.faiz0033.faizstore.BuildConfig
import com.faiz0033.faizstore.data.remote.api.ImgBBApiService
import com.faiz0033.faizstore.domain.repository.ImageUploadRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ImageUploadRepositoryImpl(
    private val imgBBApiService: ImgBBApiService
) : ImageUploadRepository {

    override suspend fun uploadImage(imageBytes: ByteArray, fileName: String): Result<String> {
        return try {
            val requestFile = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", fileName, requestFile)

            val response = imgBBApiService.uploadImage(
                apiKey = BuildConfig.IMGBB_API_KEY,
                image = body
            )

            if (response.success) {
                Result.success(response.data.url)
            } else {
                Result.failure(Exception("Image upload failed with status: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
