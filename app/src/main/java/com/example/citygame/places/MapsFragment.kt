package com.example.citygame.places

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.citygame.BuildConfig.MAPS_API_KEY
import com.example.citygame.R
import com.example.citygame.profile.ProfileFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode

private const val LOCATION_PERMISSION_REQUEST_CODE = 110
private const val TAG = "MapsFragment"

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap

    private lateinit var auth: FirebaseAuth
    private lateinit var  database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid : String



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_maps, container, false)


        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser!!.uid

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        return rootView
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val origin = LatLng(37.4319983, -122.104)
//        googleMap.addMarker(MarkerOptions().position(origin).title("stary_rynek"))

//        val destination = LatLng(37.4329983, -122.107)
//        googleMap.addMarker(MarkerOptions().position(destination).title("stary_rynek"))

        readLocationsFromDB()
      //  requestForPermissionAndEnableLocation()

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, DEFAULT_ZOOM))


        googleMap.setOnMarkerClickListener {
            Log.i(TAG, it.toString())
            true
        }

    }



    private fun readLocationsFromDB() {
        val locationsRef = databaseRef.child("locations")
        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.i(TAG, "FIREBASE READING")
                for (snapshot in dataSnapshot.children) {
                    Log.i(TAG, snapshot.toString())
                    val key = snapshot.key
                    val value = snapshot.value
                    val location = LatLng(value.)

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.i(ProfileFragment.TAG, databaseError.details)
            }
        }

        locationsRef.addListenerForSingleValueEvent(valueEventListener)
    }


    private fun requestDirections(origin: LatLng, destination: LatLng) {
        val directionsTask = DirectionsTask(googleMap)
        directionsTask.execute(origin, destination)
    }


    private class DirectionsTask(private val map: GoogleMap) :
        AsyncTask<LatLng, Void, DirectionsResult>() {

        override fun doInBackground(vararg params: LatLng): DirectionsResult {
            val origin = com.google.maps.model.LatLng(params[0].latitude, params[0].longitude)
            val destination =
                com.google.maps.model.LatLng(params[1].latitude, params[1].longitude)

            return DirectionsApi.newRequest(getGeoApiContext())
                .mode(TravelMode.WALKING)
                .origin(origin)
                .destination(destination)
                .await()
        }

        override fun onPostExecute(result: DirectionsResult) {
            val route = result.routes[0]
            val polylineOptions = PolylineOptions()
            polylineOptions.color(Color.RED)
            val points = route.overviewPolyline.decodePath()
            for (point in points) {
                polylineOptions.add(LatLng(point.lat, point.lng))
            }
            map.addPolyline(polylineOptions)
        }

        private fun getGeoApiContext(): GeoApiContext {
            val geoApiContext = GeoApiContext.Builder()
                .apiKey(MAPS_API_KEY)
                .queryRateLimit(10)
                .build()

            return geoApiContext
        }
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

                    requestDirections(userLatLng, markerLatLng)
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

