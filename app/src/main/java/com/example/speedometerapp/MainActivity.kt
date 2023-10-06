package com.example.speedometerapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.speedometerapp.databinding.ActivityMainBinding
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var binding: ActivityMainBinding

    private lateinit var fineLocation: String
    private lateinit var coarseLocation: String
    private var granted = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestLocationPermission()

        fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
        granted = PackageManager.PERMISSION_GRANTED

        // Initialize location manager and listener
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        locationListener = LocationListener { location ->
            // Calculate speed in meters per second
            if (location.hasSpeed()) {
                val speed = location.speed * 3.6 // Convert to km/h
                binding.speedTextView.text = "%.0f".format(speed)
            } else {
                Log.d("Speed", "Speed data not available")
            }
        }

        if (ActivityCompat.checkSelfPermission(this, fineLocation) != granted &&
            ActivityCompat.checkSelfPermission(this, coarseLocation) != granted) {

        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            10, // Update every 1 second
            0f,   // Update whenever there's a location change
            locationListener
        )
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            0
        )
    }
}
