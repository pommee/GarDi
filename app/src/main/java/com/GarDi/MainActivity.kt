package com.GarDi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.GarDi.ui.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNav.setOnNavigationItemSelectedListener(navListener)

        val fab: FloatingActionButton = findViewById(R.id.floatButton)
        fab.setOnClickListener {
            val intent = Intent(this, Camera::class.java)
            startActivity(intent)
        }
    }

    private val navListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    val intent = Intent(this, Camera::class.java)
                    startActivity(intent)
                }
                R.id.navigation_notifications -> {
                    val uri = "geo:447181.727,6209886.099?q=Recycling Center near me"
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
                }
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, HomeFragment()).commit()
                }
            }
            true
        }
}