package com.onebr.tv

import com.google.gson.annotations.SerializedName

data class MediaResponse(
    @SerializedName("results") val results: List<MediaItem>?
)

data class MediaItem(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("poster_path") val poster_path: String?,
    @SerializedName("backdrop_path") val backdrop_path: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("vote_average") val vote_average: Double?,
    @SerializedName("media_type") val media_type: String?
)
