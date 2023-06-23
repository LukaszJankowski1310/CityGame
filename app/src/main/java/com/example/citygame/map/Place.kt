package com.example.citygame.map

import android.location.Location
import com.google.android.gms.maps.model.Marker

data class Place(
    val id : String,
    val title : String,
    val description : String,
    val location : Location,
    val marker: Marker,
    var visited : Boolean,
    val image : String
)


