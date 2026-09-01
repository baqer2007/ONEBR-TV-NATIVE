package com.onebr.tv

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/trending")
    suspend fun getTrendingMedia(@Query("page") page: Int = 1): MediaResponse

    companion object {
        private const val BASE_URL = "https://onebr-backend.vercel.app/"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
