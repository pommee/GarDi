package com.GarDi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.GarDi.Models.MaterialHandler
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomAppBar = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        val button: Button = findViewById(R.id.button2)
        button.setOnClickListener(View.OnClickListener {
            val uri = "geo:447181.727,6209886.099?q=Recycling Center near me"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
        })

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val fab: FloatingActionButton = findViewById(R.id.floatButton);
        fab.setOnClickListener {
            val intent = Intent(this, Camera::class.java)
            startActivity(intent)
        }
    }
}