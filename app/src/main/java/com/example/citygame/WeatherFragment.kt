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
    private lateinit var weatherCondition : TextView
    private lateinit var progressBar : ProgressBar







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
        weatherCondition = view.findViewById(R.id.weather_condition)
        progressBar = view.findViewById(R.id.progressBar)





        viewModel.getWeather().observe(viewLifecycleOwner, Observer {
            Log.i(TAG, it.toString())
            displayWeather()
        })


        return view
    }


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

        weatherTemp.text = weather.current.temp_c.toString()
        weatherCondition.text = weather.current.condition.text
    }

}