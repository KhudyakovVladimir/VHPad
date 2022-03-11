package com.khudyakovvladimir.vhcloudnotepad.model.database.weather

data class Weather (
    var name: String,
    var weather: ArrayList<WeatherResponse>,
    val wind: Wind,
    val main: Temperature
    )