package com.example.citygame.map

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.maps.model.Distance

class MapViewModel : ViewModel() {

    private  var chosenPlace : MutableLiveData<Place?> = MutableLiveData()
    private var loadedLocations : MutableLiveData<ArrayList<Place>> = MutableLiveData(ArrayList())

    private var distance : Float? = null
    private var chosenDestinationPolyline : Polyline? = null
    private var travelTime : String? = null


    fun getChosenPlace(): MutableLiveData<Place?> {
        return chosenPlace
    }

    fun setChosenPlace(place: Place?) {
        chosenPlace.value = place
    }


    fun insertLocation(place : Place) {
        loadedLocations.value?.add(place)
    }

    fun getLocations(): MutableLiveData<ArrayList<Place>> {
        return loadedLocations
    }


    fun getPlaceByMarker(marker: Marker) : Place? {
        for (loc in loadedLocations.value!!) {
            Log.i("MAPVIEWMODEL", loc.marker.id)
        }

        return loadedLocations.value?.find {it.marker.id == marker.id}

    }

    fun getDistance() : Float? {
        return distance
    }

    fun setDistance(d: Float?) {
        distance = d
    }

    fun setChosenDestinationPolyline(polyline: Polyline) {
        chosenDestinationPolyline = polyline
    }

    fun getChosenDestinationPolyline() : Polyline? {
        return chosenDestinationPolyline
    }

    fun setTravelTime(travelTime: String?) {
        this.travelTime = travelTime
    }

    fun getTravelTime() : String? {
        return travelTime
    }

}