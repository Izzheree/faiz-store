package com.faiz0033.faizstore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.faiz0033.faizstore.data.local.entity.LaptopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LaptopDao {

    @Query("SELECT * FROM laptops")
    fun getAllLaptops(): Flow<List<LaptopEntity>>

    @Query("SELECT * FROM laptops WHERE id = :id")
    fun getLaptopById(id: String): Flow<LaptopEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaptops(laptops: List<LaptopEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaptop(laptop: LaptopEntity): Long

    @Query("DELETE FROM laptops")
    suspend fun deleteAllLaptops(): Int

    @Query("DELETE FROM laptops WHERE id = :id")
    suspend fun deleteLaptopById(id: String): Int
}
