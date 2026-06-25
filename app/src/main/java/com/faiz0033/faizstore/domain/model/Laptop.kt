package com.faiz0033.faizstore.domain.model

data class Laptop(
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
    val stock: Int?,
    val ownerEmail: String
)
