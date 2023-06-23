package com.example.citygame.loaddata

data class PlaceInfo(
    val id : String,
    val title : String,
    val description : String,
    val latitude : Double,
    val longitude : Double,
    val imageURL : String,
    val imageResId : Int
)
