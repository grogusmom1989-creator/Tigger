package com.example.data

import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(
    val main: WeatherMain,
    val weather: List<WeatherDescription>,
    val name: String
)

data class WeatherMain(val temp: Float?)
data class WeatherDescription(val description: String)

interface WeatherApiService {
    @GET("weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}
