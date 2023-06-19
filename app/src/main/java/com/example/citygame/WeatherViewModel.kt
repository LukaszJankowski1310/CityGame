package com.example.citygame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.citygame.api.DataRepository
import com.example.citygame.api.model.Weather

class WeatherViewModel : ViewModel() {
    private val repository : DataRepository = DataRepository()
    private lateinit var weather : MutableLiveData<Weather>


    fun callWeatherRequest() {
        weather = repository.weatherHttpRequest() as MutableLiveData<Weather>
    }

    fun getWeather() : LiveData<Weather> {
        return weather
    }


}