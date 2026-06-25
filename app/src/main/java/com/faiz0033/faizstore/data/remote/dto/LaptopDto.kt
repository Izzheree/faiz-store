package com.faiz0033.faizstore.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LaptopDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "brand") val brand: String,
    @Json(name = "price") val price: Double,
    @Json(name = "imageUrl") val imageUrl: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "processor") val processor: String?,
    @Json(name = "ram") val ram: String?,
    @Json(name = "storage") val storage: String?,
    @Json(name = "category") val category: String?,
    @Json(name = "stock") val stock: Int?
)
