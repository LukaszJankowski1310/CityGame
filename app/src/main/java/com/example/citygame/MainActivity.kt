package com.example.citygame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.citygame.auth.LoginActivity
import com.example.citygame.map.MapsFragment
import com.example.citygame.profile.AchievementsFragment
import com.example.citygame.profile.ProfileFragment
import com.example.citygame.profile.VisitedPlacesFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navigationView : NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var actionBarDrawerToggle : ActionBarDrawerToggle
    private lateinit var fragmentContainer : FrameLayout

    private lateinit var mapFragment : MapsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        fragmentContainer = findViewById(R.id.fragment_container)


        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mapFragment = MapsFragment()


        replaceFragment(mapFragment, R.string.map, false)

        navigationView.setNavigationItemSelectedListener {menuItem ->
            menuItem.isChecked = true

            when(menuItem.itemId) {

                R.id.nav_map -> {
                    Toast.makeText(applicationContext, "Map", Toast.LENGTH_SHORT).show()
                    replaceFragment(mapFragment, R.string.map)
                }

                R.id.nav_visited_places -> {
                    Toast.makeText(applicationContext, "Visited places", Toast.LENGTH_SHORT).show()
                    replaceFragment(VisitedPlacesFragment(), R.string.visited_places)
                }

                R.id.nav_your_profile -> {
                    Toast.makeText(applicationContext, "Your profile", Toast.LENGTH_SHORT).show()
                    replaceFragment(ProfileFragment(), R.string.your_profile)
                }

                R.id.nav_your_achievements -> {
                    Toast.makeText(applicationContext, "Achievements", Toast.LENGTH_SHORT).show()
                    replaceFragment(AchievementsFragment(), R.string.achievements)
                }


                R.id.nav_check_weather -> {
                    Toast.makeText(applicationContext, "Weather", Toast.LENGTH_SHORT).show()
                    replaceFragment(WeatherFragment(), R.string.weather)
                }

                R.id.nav_logout -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Confirmation")
                    builder.setMessage("Are you sure you want to logout?")

                    builder.setPositiveButton("Yes") { dialog, _ ->
                        val intent = Intent(this, LoginActivity::class.java)
                        auth.signOut()
                        startActivity(intent)
                        dialog.dismiss()
                        finish()
                    }
                    builder.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }

                    val dialog: AlertDialog = builder.create()
                    dialog.show()

                }
            }

             true
        }



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
                val uid : String = currentUser.uid
                val s = dataSnapshot.value
                Log.i("USER_ID", uid.toString())
                Log.i("dane", s.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error here
            }
        }




        rootRef.addValueEventListener(valueEventListener)





    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun replaceFragment(fragment: Fragment, title: Int, addToBackStack : Boolean = true) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(title.toString())
        }

        fragmentTransaction.commit()
        drawerLayout.closeDrawer(GravityCompat.START)
        setTitle(title)
    }

}