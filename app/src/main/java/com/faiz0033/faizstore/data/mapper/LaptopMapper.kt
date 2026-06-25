package com.faiz0033.faizstore.data.mapper

import com.faiz0033.faizstore.data.local.entity.LaptopEntity
import com.faiz0033.faizstore.data.remote.dto.LaptopDto
import com.faiz0033.faizstore.domain.model.Laptop

fun LaptopEntity.toDomain(): Laptop {
    return Laptop(
        id = id,
        name = name,
        brand = brand,
        price = price,
        imageUrl = imageUrl,
        description = description,
        processor = processor,
        ram = ram,
        storage = storage,
        category = category,
        stock = stock,
        ownerEmail = ownerEmail
    )
}

/**
 * Convert API response (DTO) to Room Entity.
 * Data coming from the API is already synced, so isSynced = 1.
 */
fun LaptopDto.toEntity(ownerEmail: String): LaptopEntity {
    return LaptopEntity(
        id = id,
        name = name,
        brand = brand,
        price = price,
        imageUrl = imageUrl,
        description = description,
        processor = processor,
        ram = ram,
        storage = storage,
        category = category,
        stock = stock,
        ownerEmail = ownerEmail,
        isSynced = 1
    )
}

/**
 * Convert domain model to Room Entity.
 * isSynced: 0 = not synced, 1 = synced.
 */
fun Laptop.toEntity(isSynced: Int = 0): LaptopEntity {
    return LaptopEntity(
        id = id,
        name = name,
        brand = brand,
        price = price,
        imageUrl = imageUrl,
        description = description,
        processor = processor,
        ram = ram,
        storage = storage,
        category = category,
        stock = stock,
        ownerEmail = ownerEmail,
        isSynced = isSynced
    )
}

fun Laptop.toDto(): LaptopDto {
    return LaptopDto(
        id = id,
        name = name,
        brand = brand,
        price = price,
        imageUrl = imageUrl,
        description = description,
        processor = processor,
        ram = ram,
        storage = storage,
        category = category,
        stock = stock,
        ownerEmail = ownerEmail
    )
}
