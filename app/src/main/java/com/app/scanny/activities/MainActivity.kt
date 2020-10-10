package com.app.scanny.activities

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.app.scanny.BuildConfig

import com.app.scanny.R
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration : AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_layout)
        navController = findNavController(R.id.nav_controller)


        when(BuildConfig.FLAVOR)
        {
            "scanny" -> {
                navController.graph = navController.navInflater.inflate(R.navigation.nav)
            }

            "bindasbol" -> {
                navController.graph = navController.navInflater.inflate(R.navigation.bindas_bol_nav)
            }
        }

        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController,appBarConfiguration)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        FirebaseApp.initializeApp(this)

        this.onBackPressedDispatcher?.addCallback( object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(!navController.navigateUp())
                {
                    finish()
                }
            }

        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home
            -> {
                if(!navController.navigateUp())
                {
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}