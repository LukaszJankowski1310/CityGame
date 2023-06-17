package com.example.citygame.places

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.citygame.BuildConfig.MAPS_API_KEY
import com.example.citygame.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode

private const val LOCATION_PERMISSION_REQUEST_CODE = 110
private const val TAG = "MapsFragment"

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_maps, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return rootView
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val loc = LatLng(37.4319983, -122.104)
        googleMap.addMarker(MarkerOptions().position(loc).title("stary_rynek"))

        val loc2 = LatLng(37.4419983, -122.124)
        googleMap.addMarker(MarkerOptions().position(loc2).title("stary_rynek"))


    }








    private fun enableLocation() {
        if (!checkPermission()) return

        googleMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    val markerLatLng = LatLng(37.4319983, -122.104)


                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, DEFAULT_ZOOM))

                    Log.i(TAG, userLatLng.toString())
                }
            }
    }

    private fun requestForPermissionAndEnableLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableLocation()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation()
            }
        }
    }


    private fun checkPermission() : Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }



    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val DEFAULT_ZOOM = 15f
    }


}




/*
{
////val katedra = LatLng(52.408333, 16.933611)
////        googleMap.addMarker(MarkerOptions().position(katedra).title("katedra"))
//////        googleMap.moveCamera(CameraUpdateFactory.newLatLng(katedra))
////
////        val ostrow_tumski = LatLng(52.408964, 16.936067)
////        googleMap.addMarker(MarkerOptions().position(ostrow_tumski).title("katedra"))
//////        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ostrow_tumski))
////
////        val jezioro_mal = LatLng( 52.404361,  16.966017)
////        googleMap.addMarker(MarkerOptions().position(jezioro_mal).title("jezioro_mal"))
//////        googleMap.moveCamera(CameraUpdateFactory.newLatLng(jezioro_mal))
////
////        val cytadela = LatLng( 52.416660,  16.913038)
////        googleMap.addMarker(MarkerOptions().position(cytadela).title("cytadela"))
//////        googleMap.moveCamera(CameraUpdateFactory.newLatLng(katedra))
}

*/

/**
 * Manipulates the map once available.
 * This callback is triggered when the map is ready to be used.
 * This is where we can add markers or lines, add listeners or move the camera.
 * In this case, we just add a marker near Sydney, Australia.
 * If Google Play services is not installed on the device, the user will be prompted to
 * install it inside the SupportMapFragment. This method will only be triggered once the
 * user has installed Google Play services and returned to the app.
 */


//private val callback = OnMapReadyCallback { googleMap ->
//
//    val stary_rynek = LatLng(52.406822, 16.934176)
//    googleMap.addMarker(MarkerOptions().position(stary_rynek).title("stary_rynek"))
//    googleMap.moveCamera(CameraUpdateFactory.newLatLng(stary_rynek))
//
//}
//
//override fun onCreateView(
//    inflater: LayoutInflater,
//    container: ViewGroup?,
//    savedInstanceState: Bundle?
//): View? {
//
//    return inflater.inflate(R.layout.fragment_maps, container, false)
//}
//
//override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//    super.onViewCreated(view, savedInstanceState)
//    val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//    mapFragment?.getMapAsync(callback)
//
//
//}
//
//override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//    if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            // Permission granted, proceed with location tracking
//        } else {
//            // Permission denied, handle accordingly
//        }
//    }
//}
