package com.example.citygame.api

import com.example.citygame.api.model.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/v1/current.json")
    fun getWeather(@Query("key") key : String, @Query("q") q : String) : Call<Weather>
}