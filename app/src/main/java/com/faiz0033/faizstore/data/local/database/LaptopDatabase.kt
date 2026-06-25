package com.faiz0033.faizstore.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.faiz0033.faizstore.data.local.dao.LaptopDao
import com.faiz0033.faizstore.data.local.entity.LaptopEntity

@Database(entities = [LaptopEntity::class], version = 2, exportSchema = false)
abstract class LaptopDatabase : RoomDatabase() {
    abstract fun laptopDao(): LaptopDao
}
