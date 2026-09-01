package com.onebr.tv

import com.google.gson.annotations.SerializedName

data class MediaResponse(
    @SerializedName("success") val success: Boolean? = false,
    @SerializedName("data") val data: List<MediaItem>? = null
)

data class MediaItem(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title") val title: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("poster") val poster: String? = null,
    @SerializedName("backdrop") val backdrop: String? = null,
    @SerializedName("overview") val overview: String? = null,
    @SerializedName("rating") val rating: Double? = null,
    @SerializedName("media_type") val media_type: String? = null
)
