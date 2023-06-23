package com.example.citygame.profile

import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.citygame.MainActivity
import com.example.citygame.R
import com.example.citygame.map.Place


class VisitedPlacesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleTextView: TextView = itemView.findViewById(R.id.title)
    val image: ImageView = itemView.findViewById(R.id.image)
}



class VisitedPlacesAdapter(private val dataList: ArrayList<VisitedPlace>, private val activity: AppCompatActivity, private val onItemClick: (visitedPlace: VisitedPlace) -> Unit )

    : RecyclerView.Adapter<VisitedPlacesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitedPlacesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.visited_place_item, parent, false)
        return VisitedPlacesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VisitedPlacesViewHolder, position: Int) {

        val currentItem = dataList[position]
        holder.titleTextView.text = currentItem.title
        Glide.with(activity)
            .load(currentItem.image)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }






}