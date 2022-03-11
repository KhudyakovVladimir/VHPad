package com.khudyakovvladimir.vhcloudnotepad.model.database.weather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather?id=571476&lang=ru&units=metric&appid=f2b71de2b11480032d732010cd4b8970")
    fun getWeather(): Call<Weather>

    @GET("weather?&lang=ru&units=metric&appid=f2b71de2b11480032d732010cd4b8970")
    fun getWeatherByCityId(@Query("id") cityID: Int): Call<Weather>

    @GET("weather?&lang=ru&units=metric&appid=f2b71de2b11480032d732010cd4b8970")
    fun getWeatherByCityName(@Query("q") cityName: String): Call<Weather>
}