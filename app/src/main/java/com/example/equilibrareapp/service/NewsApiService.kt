package com.example.equilibrareapp.service

import com.example.equilibrareapp.model.Article
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class NewsResponse(
    val status: String,

    val totalResults: Int,

    val articles: List<Article>
)

interface NewsApiService {
    @GET("v2/everything")
    fun getDailyNews(
        @Query("q") query: String,
        @Query("pageSize") pageSize: Int = 1,
        @Query("apiKey") apiKey: String
    ): Call<NewsResponse>
}