package com.faiz0033.faizstore.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImgBBResponseDto(
    @Json(name = "data") val data: ImgBBDataDto,
    @Json(name = "success") val success: Boolean,
    @Json(name = "status") val status: Int
)

@JsonClass(generateAdapter = true)
data class ImgBBDataDto(
    @Json(name = "id") val id: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "url") val url: String,
    @Json(name = "display_url") val displayUrl: String?,
    @Json(name = "delete_url") val deleteUrl: String?
)
