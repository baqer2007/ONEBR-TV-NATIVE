package com.onebr.tv.network

import com.onebr.tv.models.ApiResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("api/trending")
    suspend fun getTrending(): ApiResponse

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
