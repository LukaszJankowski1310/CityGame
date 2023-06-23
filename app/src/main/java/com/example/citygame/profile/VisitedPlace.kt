package com.example.citygame.profile

import android.location.Location
import com.google.android.gms.maps.model.Marker

data class VisitedPlace(
    val id : String,
    val title : String,
    val description : String,
    val image : String
)
