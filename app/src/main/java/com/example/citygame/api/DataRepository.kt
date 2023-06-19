package com.example.citygame.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.citygame.api.model.Weather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataRepository {

    private val  apiService : ApiService = RetrofitInstance.api

    fun weatherHttpRequest() : LiveData<Weather> {
        Log.i("APICALL", "APICALL")
        val data: MutableLiveData<Weather> = MutableLiveData<Weather>()
        apiService.getWeather("51bb583c4fed47ea8b3230028231806", "Poznan")
            .enqueue(object : Callback<Weather> {
                override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                    Log.i("SUCCESS", response.code().toString())

                    if (response.isSuccessful) {
                        data.value = response.body();
                        Log.i("SUCCESS", data.value.toString())
                    }
                }
                override fun onFailure(call: Call<Weather>, t: Throwable) {
                    Log.i("FAILURE", call.request().toString())
                    t.message?.let { Log.i("ERR", it) }
                }
            })

        return data
    }

}