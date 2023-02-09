package com.example.rangkul.data.retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("service/containsprofanity")
    suspend fun getProfanityCheck(
        @Query("text") caption: String
    ): Response<String>
}