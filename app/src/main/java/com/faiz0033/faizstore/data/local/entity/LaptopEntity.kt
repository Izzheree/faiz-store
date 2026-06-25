package com.faiz0033.faizstore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "laptops")
data class LaptopEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val brand: String,
    val price: Double,
    val imageUrl: String?,
    val description: String?,
    val processor: String?,
    val ram: String?,
    val storage: String?,
    val category: String?,
    val stock: Int?
)
