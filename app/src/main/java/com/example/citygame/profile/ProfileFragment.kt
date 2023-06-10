package com.example.citygame.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
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
import androidx.appcompat.app.AlertDialog
import com.example.citygame.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var  database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference

    private lateinit var textViewEmail : TextView
    private lateinit var textViewName : TextView
    private lateinit var textViewSurname : TextView

    private lateinit var imageViewUserPhoto : ImageView
    private lateinit var buttonChangePhoto : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val userRef = databaseRef.child("users").child(currentUser!!.uid)

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
                // Handle any errors that occurred
            }
        }

        userRef.addValueEventListener(valueEventListener)
        return view
    }

    override fun onStart() {
        super.onStart()
        buttonChangePhoto.setOnClickListener{
            selectImage()
        }
    }


    private fun selectImage() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Image")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePhotoIntent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
                1 -> {
                    val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK)
                }
            }
        }
        builder.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val extras = data?.extras
                    val imageBitmap = extras?.get("data") as Bitmap
                    imageViewUserPhoto.setImageBitmap(imageBitmap)
                }
                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri: Uri? = data?.data
                    imageViewUserPhoto.setImageURI(selectedImageUri)
                }
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
    }

}