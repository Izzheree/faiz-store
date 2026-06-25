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
        stock = stock
    )
}

fun LaptopDto.toEntity(): LaptopEntity {
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
        stock = stock
    )
}

fun Laptop.toEntity(): LaptopEntity {
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
        stock = stock
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
        stock = stock
    )
}
