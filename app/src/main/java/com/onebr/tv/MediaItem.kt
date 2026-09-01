package com.onebr.tv

import com.google.gson.annotations.SerializedName

data class MediaResponse(
    @SerializedName("results") val results: List<MediaItem>? = null
)

data class MediaItem(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title") val title: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("poster_path") val poster_path: String? = null,
    @SerializedName("media_type") val media_type: String? = null
)
