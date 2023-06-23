package com.example.citygame.loaddata


import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.citygame.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class LoadDataActivity : AppCompatActivity() {
    val places: List<PlaceInfo> = listOf(
        PlaceInfo(
            "1",
            "Old Market Square",
            "The Old Market Square is the heart of Poznań and a popular tourist attraction.",
            52.4068,
            16.9332,
            "old_market_square.jpg",
            R.drawable.old_market_square
        ),
        PlaceInfo(
            "2",
            "Poznań Cathedral",
            "Poznań Cathedral is an impressive architectural landmark in the city.",
            52.4080,
            16.9334,
            "poznan_cathedral.jpg",
            R.drawable.poznan_cathedral
        ),
        PlaceInfo(
            "3",
            "Malta Lake",
            "Malta Lake is a popular recreational area in Poznań, offering various water sports and attractions.",
            52.4326,
            16.9728,
            "malta_lake.jpg",
            R.drawable.malta_lake
        ),
        PlaceInfo(
            "4",
            "Stary Browar",
            "Stary Browar is a modern shopping center housed in a renovated historic brewery building.",
            52.4082,
            16.9315,
            "stary_browar.jpg",
            R.drawable.stary_browar
        ),
        PlaceInfo(
            "5",
            "Poznań Town Hall",
            "Poznań Town Hall is a beautiful Renaissance-style building in the city center.",
            52.4070,
            16.9330,
            "poznan_town_hall.jpg",
            R.drawable.poznan_town_hall
        ),
        PlaceInfo(
            "6",
            "Cytadela Park",
            "Cytadela Park is a large park with historic fortifications and beautiful green spaces.",
            52.4044,
            16.8993,
            "cytadela_park.jpg",
            R.drawable.cytadela_park
        ),
        PlaceInfo(
            "7",
            "Poznań Palm House",
            "Poznań Palm House is a botanical garden featuring a variety of tropical and subtropical plants.",
            52.4144,
            16.9147,
            "poznan_palm_house.jpg",
            R.drawable.poznan_palm_house
        ),
        PlaceInfo(
            "8",
            "Warta River",
            "Warta River is the main river flowing through Poznań, offering scenic views and recreational activities.",
            52.4196,
            16.9415,
            "warta_river.jpg",
            R.drawable.warta_river
        ),

        PlaceInfo(
            "10",
            "Royal Castle",
            "The Royal Castle is a historic landmark in Poznań, known for its impressive architecture.",
            52.4097,
            16.9322,
            "royal_castle.jpg",
            R.drawable.royal_castle
        )
    )


    val places2 : List<PlaceInfo> =  listOf(
        PlaceInfo(
        "10",
        "Royal Castle",
        "The Royal Castle is a historic landmark in Poznań, known for its impressive architecture.",
        37.4223936,
        -122.084922,
        "royal_castle.jpg",
        R.drawable.royal_castle
    ),
        PlaceInfo(
            "8",
            "Warta River",
            "Warta River is the main river flowing through Poznań, offering scenic views and recreational activities.",
            37.4233936,
            -122.085922,
            "warta_river.jpg",
            R.drawable.warta_river
        ),

        )



    private lateinit var auth: FirebaseAuth
    private lateinit var  database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid : String

    private lateinit var imageView : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_data)

        imageView = findViewById(R.id.imageView)

        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser!!.uid



//        val imageFileName: String = "logo.png"
    //    uploadImageToFirebaseStorage(imageUri, imageFileName)

        for (place in places2) {
            val imageUri = Uri.parse("android.resource://$packageName/${place.imageResId}")
            uploadImageToFirebaseStorage(imageUri, place.imageURL, place)
        }

    }

    private fun readImage() {
        val imagesRef = databaseRef.child("images")

        imagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val downloadUrl = dataSnapshot.getValue(String::class.java)

                    Glide.with(this@LoadDataActivity)
                        .load(downloadUrl)
                        .into(imageView)
                }



            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



    }


    private fun uploadImageToFirebaseStorage(imageUri: Uri, imageFileName : String, place : PlaceInfo) {
        val storageRef = FirebaseStorage.getInstance().reference
        val databaseRef = FirebaseDatabase.getInstance().reference

        val imageRef = storageRef.child("images/$imageFileName")
        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Image upload successful, retrieve the download URL
                imageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        // Get the download URL
                        val placeImage = uri.toString()

                        val locationDatabaseRef = databaseRef.child("places").child(UUID.randomUUID().toString())
                        val placeData = HashMap<String, Any>()

                        placeData["title"] = place.title
                        placeData["description"] = place.description
                        placeData["latitude"] = place.latitude
                        placeData["longitude"] = place.longitude
                        placeData["image"] = placeImage


                        locationDatabaseRef.setValue(placeData)
                            .addOnSuccessListener {
                                // The download URL is stored in the database successfully
                            }
                            .addOnFailureListener { e ->
                                // Handle the failure
                            }
                    }
                    .addOnFailureListener { e ->
                        // Handle the failure
                    }
            }
            .addOnFailureListener { e ->
                // Handle the failure
            }

    }
}