package com.example.data

import retrofit2.http.GET

data class NewsItem(
    val title: String,
    val link: String,
    val description: String
)

interface NewsApiService {
    // Placeholder RSS fetch - in real app, use an RSS parser
    @GET("beauty-news")
    suspend fun getBeautyNews(): List<NewsItem>
}
