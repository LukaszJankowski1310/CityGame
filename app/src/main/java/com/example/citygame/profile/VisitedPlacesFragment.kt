package com.example.citygame.profile

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView

import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.citygame.MainActivity
import com.example.citygame.R
import com.example.citygame.map.Place
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private const val TAG = "Visited Places Fragment"



class VisitedPlacesFragment : Fragment() {



    private lateinit var visitedPlacesRV : RecyclerView
    private lateinit var adapter : RecyclerView.Adapter<VisitedPlacesViewHolder>
    private lateinit var viewModel : VisitedPlacesViewModel

    private lateinit var auth: FirebaseAuth
    private lateinit var  database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[VisitedPlacesViewModel::class.java]
        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser!!.uid


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_visited_places, container, false)
        visitedPlacesRV = view.findViewById(R.id.visitedPlacesRV)
        visitedPlacesRV.layoutManager = LinearLayoutManager(requireContext())

        adapter = VisitedPlacesAdapter(arrayListOf(), requireActivity() as AppCompatActivity) {
                id -> showCustomPopupDialog(id)
        }
        visitedPlacesRV.adapter = adapter


        viewModel.getVisitedPlaces().observe(viewLifecycleOwner, Observer {list ->
            Log.i("OBSERVE", "CHANGE $list")
            adapter = VisitedPlacesAdapter(list, requireActivity() as AppCompatActivity) {
                    id -> showCustomPopupDialog(id)
            }
            visitedPlacesRV.adapter = adapter

        })



        val visitedPlacesRef = databaseRef.child("users").child(uid).child("visited_places")

        visitedPlacesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    Log.i(TAG, snapshot.toString())
                    val id = snapshot.key as String
                    val title = snapshot.child("title").value as String
                    val description = snapshot.child("description").value as String
                    val image = snapshot.child("image").value as String

                    val visitedPlace = VisitedPlace(id, title, description, image)
                    viewModel.insertVisitedPlace(visitedPlace)


                    adapter = VisitedPlacesAdapter(viewModel.getVisitedPlaces().value!!, requireActivity() as AppCompatActivity) {
                            place -> showCustomPopupDialog(place)
                    }
                    visitedPlacesRV.adapter = adapter


                }



            }

            override fun onCancelled(error: DatabaseError) {
               Log.i("TAG", error.details)
            }

        })



        return view
    }


    private fun showCustomPopupDialog(visitedPlace: VisitedPlace) {

        val dialog = Dialog(requireContext());
        dialog.setContentView(R.layout.pop_up)

        val textTitle = dialog.findViewById<TextView>(R.id.textTitle)
        val textMessage = dialog.findViewById<TextView>(R.id.textMessage)
        val image = dialog.findViewById<ImageView>(R.id.image)
        textTitle.text = visitedPlace.title
        Glide.with(this)
            .load(visitedPlace.image)
            .into(image)

        textMessage.text = visitedPlace.description
        dialog.show()
    }


}