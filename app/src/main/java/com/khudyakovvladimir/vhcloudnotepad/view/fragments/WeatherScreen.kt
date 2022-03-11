package com.khudyakovvladimir.vhcloudnotepad.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
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
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import kotlin.math.roundToInt

class WeatherScreen: Fragment() {

    lateinit var textViewWeatherCity: TextView
    lateinit var textViewTemperature: TextView
    lateinit var textViewDescription: TextView
    lateinit var textViewPressure: TextView
    lateinit var textViewWind: TextView
    lateinit var textView5: TextView
    lateinit var textView6: TextView
    lateinit var imageViewPressure: ImageView

    lateinit var imageViewWeather: ImageView
    lateinit var imageViewWind: ImageView

    lateinit var sharedPreferences: SharedPreferences

    lateinit var city: String
    lateinit var countryCode: String

    override fun onAttach(context: Context) {
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (sharedPreferences.contains("city")) {
            city = sharedPreferences.getString("city", "").toString()
            if(sharedPreferences.contains("countryCode")) {
                countryCode = sharedPreferences.getString("countryCode", "").toString()
            }
        }
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.weather_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewWeatherCity = view.findViewById(R.id.textViewCity)
        textViewTemperature = view.findViewById(R.id.textViewTemperature)
        textViewDescription = view.findViewById(R.id.textViewDescription)
        textViewPressure = view.findViewById(R.id.textViewPressure)
        textViewWind = view.findViewById(R.id.textViewWind)
        textView5 = view.findViewById(R.id.textView5)
        textView6 = view.findViewById(R.id.textView6)
        imageViewPressure = view.findViewById(R.id.imageViewPressure)

        imageViewWeather = view.findViewById(R.id.imageViewWeather)
        imageViewWind = view.findViewById(R.id.imageViewWind)

        context?.retrofit?.create(WeatherApi::class.java)?.getWeatherByCityName("$city,$countryCode")?.enqueue(object : Callback<Weather>{
            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                if(response.isSuccessful) {
                    CoroutineScope(Dispatchers.Main).launch {

                        val city = response.body()?.name
                        val temperature = response.body()?.main?.temp?.roundToInt().toString()
                        val description = response.body()?.weather?.get(0)?.description
                        val pressure = response.body()?.main?.pressure?.times(0.75)?.toInt().toString()
                        val wind = response.body()?.wind?.speed?.roundToInt().toString()
                        val icon = response.body()?.weather?.get(0)?.icon

                        textViewWeatherCity.text = city
                        textViewTemperature.text = temperature
                        textViewDescription.text = description
                        textViewPressure.text = pressure
                        textViewWind.text = wind

                        textView5.text = "o"
                        textView6.text = "C"

                        imageViewPressure.setImageResource(R.drawable.atmospheric_pressure_two)
                        imageViewWind.setImageResource(R.drawable.wind)

                        when(icon) {
                            "01d" -> { imageViewWeather.setImageResource(R.drawable.weather_01d) }
                            "01n" -> { imageViewWeather.setImageResource(R.drawable.weather_01n) }
                            "02d" -> { imageViewWeather.setImageResource(R.drawable.weather_02d) }
                            "02n" -> { imageViewWeather.setImageResource(R.drawable.weather_02n) }
                            "03d" -> { imageViewWeather.setImageResource(R.drawable.weather_03d) }
                            "03n" -> { imageViewWeather.setImageResource(R.drawable.weather_03n) }
                            "04d" -> { imageViewWeather.setImageResource(R.drawable.weather_04d) }
                            "04n" -> { imageViewWeather.setImageResource(R.drawable.weather_04n) }
                            "09d" -> { imageViewWeather.setImageResource(R.drawable.weather_09d) }
                            "09n" -> { imageViewWeather.setImageResource(R.drawable.weather_09n) }
                            "10d" -> { imageViewWeather.setImageResource(R.drawable.weather_10d) }
                            "10n" -> { imageViewWeather.setImageResource(R.drawable.weather_10n) }
                            "11d" -> { imageViewWeather.setImageResource(R.drawable.weather_11d) }
                            "11n" -> { imageViewWeather.setImageResource(R.drawable.weather_11n) }
                            "13d" -> { imageViewWeather.setImageResource(R.drawable.weather_13d) }
                            "13n" -> { imageViewWeather.setImageResource(R.drawable.weather_13n) }
                            "50d" -> { imageViewWeather.setImageResource(R.drawable.weather_50d) }
                            "50n" -> { imageViewWeather.setImageResource(R.drawable.weather_50n) }
                        }

                        fadeInView(textViewWeatherCity)
                        fadeInView(textViewTemperature)
                        fadeInView(textViewDescription)
                        fadeInView(textViewPressure)
                        fadeInView(imageViewWeather)
                        fadeInView(textView5)
                        fadeInView(textView6)
                        fadeInView(imageViewPressure)

                    }
                }else {
                    Log.d("TAG", "RESPONSE_CODE = ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Weather>, t: Throwable) {
                Log.d("TAG", "FAILURE = $t")
                textView5.visibility = View.INVISIBLE
                textView6.visibility = View.INVISIBLE
                imageViewPressure.visibility = View.INVISIBLE
                imageViewWeather.setImageResource(R.drawable.connection_interrupt)
                textViewWeatherCity.text = "Service unavailable"
                textViewDescription.textSize = 20F
                textViewDescription.text = "Please check your connection"
            }
        })

    }

    private fun fadeInView(img: View) {
        val fadeIn: Animation = AlphaAnimation(0F, 1F)
        fadeIn.interpolator = AccelerateInterpolator()
        fadeIn.duration = 300
        fadeIn.setAnimationListener(object : AnimationListener {
            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationStart(animation: Animation) {}
        })
        img.startAnimation(fadeIn)
    }
}
