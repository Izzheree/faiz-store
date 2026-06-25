package com.faiz0033.faizstore.data.remote.api

import com.faiz0033.faizstore.data.remote.dto.LaptopDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface LaptopApiService {
    @GET("laptops")
    suspend fun getAllLaptops(): List<LaptopDto>

    @POST("laptops")
    suspend fun addLaptop(@Body laptop: LaptopDto): LaptopDto

    @PUT("laptops/{id}")
    suspend fun updateLaptop(@Path("id") id: String, @Body laptop: LaptopDto): LaptopDto

    @DELETE("laptops/{id}")
    suspend fun deleteLaptop(@Path("id") id: String)
}
