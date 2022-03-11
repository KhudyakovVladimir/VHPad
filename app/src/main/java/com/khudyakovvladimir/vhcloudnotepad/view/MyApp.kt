package com.khudyakovvladimir.vhcloudnotepad.view

import android.app.Application
import android.content.Context
import com.khudyakovvladimir.vhcloudnotepad.dependencyinjection.AppComponent
import com.khudyakovvladimir.vhcloudnotepad.dependencyinjection.DaggerAppComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApp : Application() {

    lateinit var appComponent: AppComponent
    private set

    lateinit var retrofit: Retrofit
    private set

    override fun onCreate() {
        super.onCreate()

        appComponent =
            DaggerAppComponent
                .builder()
                .application(this)
                .build()

        retrofit = Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }
}

val Context.appComponent: AppComponent
get() = when(this) {
    is MyApp -> appComponent
    else -> applicationContext.appComponent
}

val Context.retrofit: Retrofit
get() = when(this) {
    is MyApp -> retrofit
    else -> applicationContext.retrofit
}