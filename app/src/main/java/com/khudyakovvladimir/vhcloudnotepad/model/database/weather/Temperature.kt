package com.khudyakovvladimir.vhcloudnotepad.model.database.weather

data class Temperature (
    val temp: Float,
    val feels_like: Float,
    val temp_min: Float,
    val temp_max: Float,
    val pressure: Int,
    val humidity: Int
        )