package com.faiz0033.faizstore.data.repository

import com.faiz0033.faizstore.data.local.dao.LaptopDao
import com.faiz0033.faizstore.data.mapper.toDomain
import com.faiz0033.faizstore.data.mapper.toDto
import com.faiz0033.faizstore.data.mapper.toEntity
import com.faiz0033.faizstore.data.remote.api.LaptopApiService
import com.faiz0033.faizstore.domain.model.Laptop
import com.faiz0033.faizstore.domain.repository.LaptopRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class LaptopRepositoryImpl(
    private val laptopDao: LaptopDao,
    private val laptopApiService: LaptopApiService
) : LaptopRepository {

    /**
     * Offline-first strategy: 
     * 1. The UI always listens to the Room Database (Single Source of Truth).
     * 2. When the flow starts collecting, we launch a background task to fetch from the API.
     * 3. If the API fetch succeeds, we clear the old data and save the new data to Room.
     * 4. Room automatically emits the new data to the UI!
     */
    override fun getLaptops(): Flow<List<Laptop>> {
        return laptopDao.getAllLaptops()
            .map { entities -> entities.map { it.toDomain() } }
            .onStart {
                // Launch a background coroutine to sync with the network
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val remoteData = laptopApiService.getAllLaptops()
                        // Replace old local data with fresh remote data
                        laptopDao.deleteAllLaptops()
                        laptopDao.insertLaptops(remoteData.map { it.toEntity() })
                    } catch (e: Exception) {
                        // If offline or API fails, do nothing. The UI will keep showing local Room data!
                    }
                }
            }
    }

    override fun getLaptopById(id: String): Flow<Laptop?> {
        return laptopDao.getLaptopById(id)
            .map { it?.toDomain() }
    }

    override suspend fun addLaptop(laptop: Laptop) {
        // Optimistic update: Add to remote API first
        val addedRemote = laptopApiService.addLaptop(laptop.toDto())
        // If successful, save to our local Room Database
        laptopDao.insertLaptop(addedRemote.toEntity())
    }

    override suspend fun updateLaptop(laptop: Laptop) {
        // Update the remote API
        val updatedRemote = laptopApiService.updateLaptop(laptop.id, laptop.toDto())
        // Update local Room Database
        laptopDao.insertLaptop(updatedRemote.toEntity())
    }

    override suspend fun deleteLaptop(id: String) {
        // Delete from API first
        laptopApiService.deleteLaptop(id)
        // If API delete succeeds, delete from local Room Database
        laptopDao.deleteLaptopById(id)
    }
}
