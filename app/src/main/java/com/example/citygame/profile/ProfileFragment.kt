package com.example.citygame.profile

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.citygame.MainActivity
import com.example.citygame.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.io.File

private const val CAMERA_PERMISSION_REQUEST_CODE = 100

class ProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var  database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid : String

    private lateinit var textViewEmail : TextView
    private lateinit var textViewName : TextView
    private lateinit var textViewSurname : TextView
    private lateinit var imageViewUserPhoto : ImageView
    private lateinit var buttonChangePhoto : Button

    private lateinit var imageUri : Uri
    private lateinit var takePictureLauncher : ActivityResultLauncher<Uri>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser!!.uid

        imageUri = createUri()
        registerPictureLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        textViewEmail = view.findViewById(R.id.emailTextView)
        textViewName = view.findViewById(R.id.nameTextView)
        textViewSurname = view.findViewById(R.id.surnameTextView)
        buttonChangePhoto = view.findViewById(R.id.button_change_photo)
        imageViewUserPhoto = view.findViewById(R.id.imageViewUserPhoto)


        val userRef = databaseRef.child("users").child(uid)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val email = dataSnapshot.child("email").getValue(String::class.java)
                val name = dataSnapshot.child("name").getValue(String::class.java)
                val surname = dataSnapshot.child("surname").getValue(String::class.java)

                textViewEmail.text = email
                textViewName.text = name
                textViewSurname.text = surname
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.i(TAG, databaseError.details)
            }
        }
        userRef.addValueEventListener(valueEventListener)

        val file : File = File(requireActivity().filesDir, "${uid}_profile_photo.jpg")
        if(file.exists()) {
            imageViewUserPhoto.setImageURI(imageUri)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        buttonChangePhoto.setOnClickListener{
            checkCameraPermissionAndOpenCamera()
        }
    }

    private fun createUri() : Uri {
        val filename = "${uid}_profile_photo.jpg"
        val imageFile : File = File(requireActivity().filesDir, filename )
        return FileProvider.getUriForFile(requireContext().applicationContext,
            "com.example.citygame.fileprovider", imageFile)
    }

    private fun registerPictureLauncher() {
        takePictureLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture(),
            ActivityResultCallback {
                if (it) {
                    imageViewUserPhoto.setImageURI(null)
                    imageViewUserPhoto.setImageURI(imageUri)
                }
            }
        )
    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            takePictureLauncher.launch(imageUri)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePictureLauncher.launch(imageUri)
            } else {
                Log.i(TAG, "Permission denied")
            }
        }
    }


    companion object {
        const val TAG = "ProfileFragment"
    }



}