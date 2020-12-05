package com.app.scanny.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.app.scanny.BuildConfig
import com.app.scanny.R
import com.app.scanny.databinding.ActivityHomeLayoutBinding
import com.app.scanny.repository.Repository.remoteConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var dataBinding : ActivityHomeLayoutBinding
    private lateinit var mInterstitialAd : InterstitialAd
    private var adShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_layout)
        navController = findNavController(R.id.nav_controller)


        when(BuildConfig.FLAVOR)
        {
            "scanny" -> {
                navController.graph = navController.navInflater.inflate(R.navigation.nav)
            }

            "bindasbol" -> {
                navController.graph = navController.navInflater.inflate(R.navigation.bindas_bol_nav)
            }

            "careercoop" -> {
                navController.graph =
                    navController.navInflater.inflate(R.navigation.career_coop_nav)
            }
        }

        setUpUi()

        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if(BuildConfig.DEBUG) 0 else 3600
        }
        remoteConfig?.setConfigSettingsAsync(configSettings)
        remoteConfig?.fetch()?.addOnCompleteListener {

            if (it.isSuccessful) {
                Log.d("Remote Config", "Success")
                remoteConfig?.activate()
            }
            else
            {
                Log.d("Remote Config", "Failure")
                Log.d("Remote Config", it.exception?.message.toString())
            }

        }

    }

    private fun setUpUi() {
        appBarConfiguration = AppBarConfiguration(navController.graph)

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        FirebaseApp.initializeApp(this)

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        mInterstitialAd.loadAd(AdRequest.Builder().build())




        NavigationUI.setupWithNavController(
            dataBinding.bottomNav,
            navController
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home
            -> {
                if (!navController.navigateUp()) {
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()

        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            if (mInterstitialAd.isLoaded && adShown.not()) {
                mInterstitialAd.show()
                adShown = true
            }
        }

    }

}