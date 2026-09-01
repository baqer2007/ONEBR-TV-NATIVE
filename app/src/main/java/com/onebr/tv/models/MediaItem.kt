package com.onebr.tv.models

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    val success: Boolean,
    val data: List<MediaItem>
)

data class MediaItem(
    val id: Long,
    val title: String,
    val overview: String?,
    val poster: String?,
    val backdrop: String?,
    @SerializedName("media_type") val mediaType: String,
    val rating: Double,
    @SerializedName("release_date") val releaseDate: String?
)
