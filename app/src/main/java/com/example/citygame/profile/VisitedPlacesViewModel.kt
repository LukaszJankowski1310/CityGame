package com.example.citygame.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.citygame.map.Place

class VisitedPlacesViewModel : ViewModel() {
    private val visitedPlaces : MutableLiveData<ArrayList<VisitedPlace>> = MutableLiveData(
        arrayListOf()
    )


    fun getVisitedPlaces() : MutableLiveData<ArrayList<VisitedPlace>> {
        return visitedPlaces
    }

    fun insertVisitedPlace(visitedPlace: VisitedPlace) {
        visitedPlaces.value?.add(visitedPlace)
        Log.i("VIEWMODEL", visitedPlaces.value.toString())

    }
}