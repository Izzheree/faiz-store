package com.faiz0033.faizstore.domain.repository

import com.faiz0033.faizstore.domain.model.Laptop
import kotlinx.coroutines.flow.Flow

interface LaptopRepository {
    fun getLaptops(): Flow<List<Laptop>>
    fun getLaptopById(id: String): Flow<Laptop?>
    
    suspend fun addLaptop(laptop: Laptop)
    suspend fun updateLaptop(laptop: Laptop)
    suspend fun deleteLaptop(id: String)
}
