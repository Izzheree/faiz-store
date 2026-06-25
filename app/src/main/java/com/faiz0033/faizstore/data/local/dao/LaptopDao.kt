package com.faiz0033.faizstore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.faiz0033.faizstore.data.local.entity.LaptopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LaptopDao {

    /**
     * Get all laptops belonging to a specific user (filtered by ownerEmail).
     */
    @Query("SELECT * FROM laptops WHERE ownerEmail = :ownerEmail")
    fun getAllLaptopsByOwner(ownerEmail: String): Flow<List<LaptopEntity>>

    @Query("SELECT * FROM laptops WHERE id = :id")
    fun getLaptopById(id: String): Flow<LaptopEntity?>

    /**
     * Get all laptops that have NOT been synced to the API yet.
     */
    @Query("SELECT * FROM laptops WHERE isSynced = 0")
    suspend fun getUnsyncedLaptops(): List<LaptopEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaptops(laptops: List<LaptopEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaptop(laptop: LaptopEntity): Long

    /**
     * Mark a laptop as synced after successfully pushing to the API.
     */
    @Query("UPDATE laptops SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String): Int

    /**
     * Delete only synced laptops for a specific user (before replacing with fresh API data).
     * Keeps unsynced items intact so they can be pushed later.
     */
    @Query("DELETE FROM laptops WHERE ownerEmail = :ownerEmail AND isSynced = 1")
    suspend fun deleteSyncedLaptopsByOwner(ownerEmail: String): Int

    @Query("DELETE FROM laptops WHERE id = :id")
    suspend fun deleteLaptopById(id: String): Int
}
