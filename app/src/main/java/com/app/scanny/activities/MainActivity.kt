package com.app.scanny.activities

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.app.scanny.BuildConfig

import com.app.scanny.R


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

    }
}