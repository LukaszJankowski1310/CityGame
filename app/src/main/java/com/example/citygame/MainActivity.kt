package com.example.citygame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    private lateinit var buttonLogOut : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val rootRef: DatabaseReference = database.reference
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }


        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val uid : String = currentUser!!.uid
                val s = dataSnapshot.value
                Log.i("dane", s.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error here
            }
        }


        buttonLogOut = findViewById(R.id.buttonLogOut)
        buttonLogOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        rootRef.addValueEventListener(valueEventListener)

    }
}