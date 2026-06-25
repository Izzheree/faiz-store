package com.faiz0033.faizstore.data.repository

import com.faiz0033.faizstore.data.local.dao.LaptopDao
import com.faiz0033.faizstore.data.mapper.toDomain
import com.faiz0033.faizstore.data.mapper.toDto
import com.faiz0033.faizstore.data.mapper.toEntity
import com.faiz0033.faizstore.data.remote.api.LaptopApiService
import com.faiz0033.faizstore.domain.model.Laptop
import com.faiz0033.faizstore.domain.repository.LaptopRepository
import com.faiz0033.faizstore.domain.repository.ImageUploadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class LaptopRepositoryImpl(
    private val laptopDao: LaptopDao,
    private val laptopApiService: LaptopApiService,
    private val imageUploadRepository: ImageUploadRepository
) : LaptopRepository {

    /**
     * Offline-first strategy: read from Room.
     * Synchronization is triggered explicitly from view models / screen lifecycle.
     */
    override fun getLaptops(ownerEmail: String): Flow<List<Laptop>> {
        return laptopDao.getAllLaptopsByOwner(ownerEmail)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getLaptopById(id: String): Flow<Laptop?> {
        return laptopDao.getLaptopById(id)
            .map { it?.toDomain() }
    }

    /**
     * Fetch latest data from remote API and update local Room DB.
     */
    override suspend fun syncRemote(ownerEmail: String) {
        try {
            // Step 1: Push any unsynced local items to the API
            syncUnsyncedLaptops()

            // Step 2: Fetch only this user's laptops from API (server-side filter)
            val myLaptops = laptopApiService.getAllLaptops(ownerEmail)

            // Step 3: Replace only synced data in Room (keep unsynced items intact)
            laptopDao.deleteSyncedLaptopsByOwner(ownerEmail)
            laptopDao.insertLaptops(myLaptops.map { it.toEntity(ownerEmail) })
        } catch (e: Exception) {
            // Offline or API error — keep using local Room database
        }
    }

    /**
     * Offline-first add:
     * 1. Save to Room immediately (isSynced = 0).
     * 2. Try to push to API. If successful, mark as synced.
     */
    override suspend fun addLaptop(laptop: Laptop) {
        // Always save locally first (isSynced = 0 means not synced)
        laptopDao.insertLaptop(laptop.toEntity(isSynced = 0))

        // Try to sync to API
        try {
            val addedRemote = laptopApiService.addLaptop(laptop.toDto())
            // API might return a different ID, so update the local entry
            laptopDao.deleteLaptopById(laptop.id)
            laptopDao.insertLaptop(addedRemote.toEntity(laptop.ownerEmail))
        } catch (e: Exception) {
            // Offline — item stays unsynced in Room
        }
    }

    /**
     * Offline-first update:
     * Save locally first, then try the API.
     */
    override suspend fun updateLaptop(laptop: Laptop) {
        laptopDao.insertLaptop(laptop.toEntity(isSynced = 0))

        try {
            val updatedRemote = laptopApiService.updateLaptop(laptop.id, laptop.toDto())
            laptopDao.insertLaptop(updatedRemote.toEntity(laptop.ownerEmail))
        } catch (e: Exception) {
            // Offline — stays unsynced
        }
    }

    /**
     * Offline-first delete:
     * Delete locally first, then try the API.
     */
    override suspend fun deleteLaptop(id: String) {
        laptopDao.deleteLaptopById(id)

        try {
            laptopApiService.deleteLaptop(id)
        } catch (e: Exception) {
            // Offline — item is already removed locally.
        }
    }

    /**
     * Push all unsynced laptops to the API.
     * Also handles uploading local cached images to ImgBB first.
     */
    override suspend fun syncUnsyncedLaptops() {
        val unsyncedItems = laptopDao.getUnsyncedLaptops()
        for (item in unsyncedItems) {
            try {
                var laptop = item.toDomain()

                // Push to MockAPI
                val isNew = laptop.id.contains("-") // UUIDs generated locally contain hyphens
                if (isNew) {
                    val addedRemote = laptopApiService.addLaptop(laptop.toDto())
                    laptopDao.deleteLaptopById(laptop.id)
                    laptopDao.insertLaptop(addedRemote.toEntity(laptop.ownerEmail))
                } else {
                    val updatedRemote = laptopApiService.updateLaptop(laptop.id, laptop.toDto())
                    laptopDao.insertLaptop(updatedRemote.toEntity(laptop.ownerEmail))
                }
            } catch (e: Exception) {
                // If one item fails, skip it and try the next
            }
        }
    }
}
