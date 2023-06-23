package com.example.citygame.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.citygame.BuildConfig
import com.example.citygame.R
import com.example.citygame.profile.ProfileFragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode

const val TAG = "Maps Fragment"

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private lateinit var viewModel: MapViewModel

    private lateinit var auth: FirebaseAuth
    private lateinit var  database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid : String


    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var distanceTV : TextView
    private lateinit var removeTraceBTN : Button
    private lateinit var titleTV : TextView
    private lateinit var timeTV : TextView

    private var popUpDisplayed = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser!!.uid

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_maps, container, false)
        distanceTV = rootView.findViewById(R.id.distanceTV)
        removeTraceBTN = rootView.findViewById(R.id.removeTraceBTN)
        titleTV = rootView.findViewById(R.id.titleTV)
        timeTV = rootView.findViewById(R.id.timeTV)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        displayTraceInformation()

        viewModel.getChosenPlace().observe(viewLifecycleOwner, Observer {chosenLocation ->
            Log.i("$TAG chosenLocation", chosenLocation.toString())
            setTrace(chosenLocation)
            viewModel.setTravelTime(null)
            displayTraceInformation()
        })


        removeTraceBTN.setOnClickListener {
            removeTrace()
        }

        return rootView
    }

    private fun displayTraceInformation() {
        val chosenPlace = viewModel.getChosenPlace().value
        if (chosenPlace != null) {
            titleTV.text = chosenPlace.title

            if (viewModel.getTravelTime() == null) {
                timeTV.text = String.format("Time : %s", "loading")
            } else {
                timeTV.text = String.format("Time : %s", viewModel.getTravelTime())
            }

            distanceTV.text = String.format("Distance : %.2f", viewModel.getDistance())

            removeTraceBTN.visibility = View.VISIBLE

        } else {
            titleTV.text = "Poznań"
            timeTV.text = "Time: "
            distanceTV.text = "Distance :"

            removeTraceBTN.visibility = View.GONE

        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        readLocationsFromDB()
        requestForPermissionAndEnableLocation()


        googleMap.setOnMarkerClickListener {
            val place = viewModel.getPlaceByMarker(it)
            popUpDisplayed = false
            viewModel.setChosenPlace(place!!)
            true
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                val chosenPlace = viewModel.getChosenPlace().value
                Log.i(TAG+" choosen place", chosenPlace.toString())

                p0.lastLocation?.let { location ->
                    if (chosenPlace != null ) {
                        if (viewModel.getDistance()!! < 15) {
                            saveVisitedPlace(chosenPlace)
                            removeTrace()
                            if (!popUpDisplayed)
                            {
                                showCustomPopupDialog(chosenPlace)
                                popUpDisplayed = true
                            }

                            return@let
                        }

                        val userLatLng = LatLng(location.latitude, location.longitude)
                        val destination = LatLng(chosenPlace.location.latitude, chosenPlace.location.longitude)

                        requestDirections(userLatLng, destination)
                        val distance = location.distanceTo(chosenPlace.location)
                        viewModel.setDistance(distance)


                        displayTraceInformation()


                    }
                }
            }
        }

        if (checkPermission()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            moveMapToUserLocalization()
        }



    }
    private fun saveVisitedPlace(place : Place) {
        if (place.visited) return

        val visitedPlaceRef = databaseRef.child("users").child(uid).child("visited_places").child(place.id)
        // preparing to save
        val placeData = HashMap<String, Any>()
        placeData["title"] = place.title
        placeData["description"] = place.description
        placeData["latitude"] = place.location.latitude
        placeData["longitude"] = place.location.longitude
        placeData["image"] = place.image

        place.visited = true

        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.rogal)
        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 50, 50, false)
        val newBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(scaledBitmap)
        place.marker.setIcon(newBitmapDescriptor)

        visitedPlaceRef.setValue(placeData)
    }

    private fun setTrace(destination: Place?) {
        if (destination == null){
            viewModel.setDistance(null)
            distanceTV.text = ""
            return
        }
        enableLocation()
        if (checkPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val userLatLng = LatLng(location.latitude, location.longitude)
                        Log.i(TAG, userLatLng.toString())
                        val des = LatLng(destination.location.latitude, destination.location.longitude)

                        requestDirections(userLatLng, des)
                        val distance = location.distanceTo(destination.location)
                        viewModel.setDistance(distance)
                        displayTraceInformation()
                    }
                }
        }
    }

    private fun moveMapToUserLocalization() {
        if (checkPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val userLatLng = LatLng(location.latitude, location.longitude)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLng))
                        googleMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM))

                    }
                }
        }
    }

    private fun readLocationsFromDB() {
        val locationsRef = databaseRef.child("places")
        val userLocationsRef = databaseRef.child("users").child(uid).child("visited_places")


        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.i("DB", "FIREBASE READING")
                for (snapshot in dataSnapshot.children) {

                    val id = snapshot.key as String
                    val lat = snapshot.child("latitude").value as Double
                    val lng = snapshot.child("longitude").value as Double
                    val title = snapshot.child("title").value as String
                    val description = snapshot.child("description").value as String
                    val image = snapshot.child("image").value as String



                    val location = LatLng(lat, lng)
                    val loc = Location(id)
                    loc.apply {
                        latitude = lat
                        longitude = lng
                    }
                    val marker = MarkerOptions()
                    marker.title(title)
                    marker.position(location)

                    userLocationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val visited: Boolean
                            if (snapshot.children.find { it.key as String == id} != null) {
                                visited = true
                            }
                            else {
                                visited = false
                                Log.i(TAG, "place not visited")
                            }
                            val m = googleMap.addMarker(marker)
                            if (m != null){
                                if (visited) {
                                    val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.rogal)
                                    val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 75, 75, false)
                                    val newBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(scaledBitmap)
                                    m.setIcon(newBitmapDescriptor)
                                }

                                val place = Place(id, title, description, loc, m, visited, image)
                                viewModel.insertLocation(place)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.i(ProfileFragment.TAG, error.details)
                        }

                    })

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.i(ProfileFragment.TAG, databaseError.details)
            }
        }

        locationsRef.addListenerForSingleValueEvent(valueEventListener)

    }



    private  fun requestDirections(origin: LatLng, destination: LatLng) {
        val directionsTask = DirectionsTask(googleMap)
        directionsTask.execute(origin, destination)
    }


    private fun removeTrace() {
        viewModel.setChosenPlace(null)
        viewModel.getChosenDestinationPolyline()?.remove()
        viewModel.setDistance(null)
        viewModel.setTravelTime(null)
    }

    private fun showCustomPopupDialog(place : Place) {
        Log.i("POPUP", "POPUP")
        val dialog = Dialog(requireContext());
        dialog.setContentView(R.layout.pop_up)

        val textTitle = dialog.findViewById<TextView>(R.id.textTitle)
        val textMessage = dialog.findViewById<TextView>(R.id.textMessage)
        val image = dialog.findViewById<ImageView>(R.id.image)
        textTitle.text = place.title
        Glide.with(this)
            .load(place.image)
            .into(image)

        textMessage.text = place.description
        dialog.show()
    }


    @SuppressLint("StaticFieldLeak")
    private inner class DirectionsTask(private val map: GoogleMap) :
        AsyncTask<LatLng, Void, DirectionsResult>() {

        @Deprecated("Deprecated in Java")
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

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: DirectionsResult) {
            val route = result.routes[0]
            val leg = route.legs[0]



            val travelTime = leg.duration.humanReadable

            val polylineOptions = PolylineOptions()
            polylineOptions.color(Color.RED)
            val points = route.overviewPolyline.decodePath()
            for (point in points) {
                polylineOptions.add(LatLng(point.lat, point.lng))
            }
            viewModel.getChosenDestinationPolyline()?.remove()

            val chosenDestinationPolyline = map.addPolyline(polylineOptions)
            viewModel.setChosenDestinationPolyline(chosenDestinationPolyline)
            viewModel.setTravelTime(travelTime)

        }

        private fun getGeoApiContext(): GeoApiContext {
            val geoApiContext = GeoApiContext.Builder()
                .apiKey(BuildConfig.MAPS_API_KEY)
                .queryRateLimit(10)
                .build()

            return geoApiContext
        }
    }


    private fun enableLocation() {
        if (!checkPermission()) return
        googleMap.isMyLocationEnabled = true
    }

    private fun requestForPermissionAndEnableLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
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



    override fun onDestroyView() {
        super.onDestroyView()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()

    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val DEFAULT_ZOOM = 15f
    }

}