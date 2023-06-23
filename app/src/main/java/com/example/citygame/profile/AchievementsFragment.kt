package com.example.citygame.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.citygame.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class AchievementsFragment : Fragment() {


    private lateinit var achievementsRV : RecyclerView
    private lateinit var adapter : RecyclerView.Adapter<AchievementsViewHolder>
    private lateinit var auth: FirebaseAuth
    private lateinit var  database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid : String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser!!.uid

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_achievements, container, false)
        achievementsRV = view.findViewById(R.id.achievementsRV)
        achievementsRV.layoutManager = LinearLayoutManager(requireContext())



        val userRef = databaseRef.child("users").child(uid)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val visitedPlaces = dataSnapshot.child("visited_places")
                val visitedPlacesNum = visitedPlaces.children.count()

                adapter = AchievementsAdapter(UserTitle.titles, visitedPlacesNum)
                achievementsRV.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.i(ProfileFragment.TAG, databaseError.details)
            }
        }
        userRef.addValueEventListener(valueEventListener)



        return view
    }



}