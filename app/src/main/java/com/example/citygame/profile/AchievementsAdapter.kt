package com.example.citygame.profile

import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.citygame.R



class AchievementsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val achievementTextView: TextView = itemView.findViewById(R.id.achievementTV)
    val image: ImageView = itemView.findViewById(R.id.image)
}


class AchievementsAdapter(private val dataList: List<String>, private val userTitle : Int)
    : RecyclerView.Adapter<AchievementsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.achievements_item, parent, false)
        return AchievementsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AchievementsViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.achievementTextView.text = currentItem

        if (position < userTitle) {
            holder.image.setBackgroundResource(R.drawable.current_achievement)
        }

        if (position == userTitle) {
            holder.image.setBackgroundResource(R.drawable.current_achievement)

            holder.achievementTextView.setTypeface(null, Typeface.BOLD)
            holder.achievementTextView.setTextColor(ContextCompat.getColor(holder.achievementTextView.context, R.color.gold))

//            textView.setTextColor(getResources().getColor(R.color.your_color));
//            textView.setTypeface(null, Typeface.BOLD);
        }


    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}