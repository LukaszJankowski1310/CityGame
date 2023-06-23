package com.example.citygame

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private const val TAG = "WeatherFragment"

class WeatherFragment : Fragment() {
    private lateinit var viewModel: WeatherViewModel

    private lateinit var weatherCity : TextView
    private lateinit var weatherImage : ImageView
    private lateinit var weatherTemp : TextView
    private lateinit var progressBar : ProgressBar



    private lateinit var tvCondition: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvWindSpeed: TextView
    private lateinit var tvVisibility: TextView
    private lateinit var tvPressure: TextView





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        viewModel.callWeatherRequest()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_weather, container, false)
        weatherCity = view.findViewById(R.id.weather_city)
        weatherImage = view.findViewById(R.id.weather_image)
        weatherTemp = view.findViewById(R.id.weather_temp)
        progressBar = view.findViewById(R.id.progressBar)



        tvCondition = view.findViewById(R.id.tvCondition)
        tvHumidity = view.findViewById(R.id.tvHumidity)
        tvWindSpeed = view.findViewById(R.id.tvWindSpeed)
        tvVisibility = view.findViewById(R.id.tvVisibility)
        tvPressure = view.findViewById(R.id.tvPressure)




        viewModel.getWeather().observe(viewLifecycleOwner, Observer {
            Log.i(TAG, it.toString())
            displayWeather()
        })


        return view
    }

//    Temperature in Celsius (temp_c)
//    Condition description (condition.text)
//    Feels like temperature in Celsius (feelslike_c)
//    Humidity (humidity)
//    Wind speed in kilometers per hour (wind_kph)
//    Visibility in kilometers (vis_km)
//    Pressure in millibars (pressure_mb)


    private fun displayWeather() {
        progressBar.visibility = View.GONE;


        val weather = viewModel.getWeather().value
        weatherCity.text = weather!!.location.name
        val url = "https://" + weather.current.condition.icon
        Log.i(TAG, url)
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(weatherImage)


        weatherTemp.text = String.format("%.1f°C", weather.current.temp_c)

        tvCondition.text = String.format("Condition: %s", weather.current.condition.text)
        tvHumidity.text = String.format("Humidity: %d", weather.current.humidity)
        tvWindSpeed.text = String.format("Wind Speed: %.2f kph", weather.current.wind_kph)
        tvVisibility.text = String.format("Visibility: %.2f km", weather.current.vis_km)
        tvPressure.text = String.format("Pressure: %.2f hPa", weather.current.pressure_mb)
    }

}