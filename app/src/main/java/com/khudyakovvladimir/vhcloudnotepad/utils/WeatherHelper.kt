package com.khudyakovvladimir.vhcloudnotepad.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.vhcloudnotepad.R
import com.khudyakovvladimir.vhcloudnotepad.model.database.weather.Weather
import com.khudyakovvladimir.vhcloudnotepad.model.database.weather.WeatherApi
import com.khudyakovvladimir.vhcloudnotepad.view.retrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.math.roundToInt

class WeatherHelper @Inject constructor(){

    fun getWeather(context: Context, imageView: ImageView, textView: TextView, city: String, countryCode: String) {
        context.retrofit.create(WeatherApi::class.java).getWeatherByCityName("$city,$countryCode")?.enqueue(object :
            Callback<Weather> {
            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                if(response.isSuccessful) {

                    CoroutineScope(Dispatchers.Main).launch {

                        val city = response.body()?.name
                        val temperature = response.body()?.main?.temp?.roundToInt().toString()
                        val description = response.body()?.weather?.get(0)?.description
                        val pressure = response.body()?.main?.pressure?.times(0.75)?.toInt().toString()
                        val icon = response.body()?.weather?.get(0)?.icon

                        textView.text = temperature

                        when(icon) {
                            "01d" -> { imageView.setImageResource(R.drawable.weather_01d) }
                            "01n" -> { imageView.setImageResource(R.drawable.weather_01n) }
                            "02d" -> { imageView.setImageResource(R.drawable.weather_02d) }
                            "02n" -> { imageView.setImageResource(R.drawable.weather_02n) }
                            "03d" -> { imageView.setImageResource(R.drawable.weather_03d) }
                            "03n" -> { imageView.setImageResource(R.drawable.weather_03n) }
                            "04d" -> { imageView.setImageResource(R.drawable.weather_04d) }
                            "04n" -> { imageView.setImageResource(R.drawable.weather_04n) }
                            "09d" -> { imageView.setImageResource(R.drawable.weather_09d) }
                            "09n" -> { imageView.setImageResource(R.drawable.weather_09n) }
                            "10d" -> { imageView.setImageResource(R.drawable.weather_10d) }
                            "10n" -> { imageView.setImageResource(R.drawable.weather_10n) }
                            "11d" -> { imageView.setImageResource(R.drawable.weather_11d) }
                            "11n" -> { imageView.setImageResource(R.drawable.weather_11n) }
                            "13d" -> { imageView.setImageResource(R.drawable.weather_13d) }
                            "13n" -> { imageView.setImageResource(R.drawable.weather_13n) }
                            "50d" -> { imageView.setImageResource(R.drawable.weather_50d) }
                            "50n" -> { imageView.setImageResource(R.drawable.weather_50n) }
                        }
                    }
                }else {
                    Log.d("TAG", "RESPONSE_CODE = ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Weather>, t: Throwable) {
                Log.d("TAG", "FAILURE = $t")
                imageView.visibility = View.INVISIBLE
                textView.text = ""
            }
        })
    }
}