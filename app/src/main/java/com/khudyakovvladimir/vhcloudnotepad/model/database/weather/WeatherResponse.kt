package com.khudyakovvladimir.vhcloudnotepad.model.database.weather

data class WeatherResponse(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)