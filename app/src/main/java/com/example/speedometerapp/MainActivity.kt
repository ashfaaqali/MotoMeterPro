package com.example.speedometerapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.speedometerapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize location manager and listener
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = LocationListener { location -> // Calculate speed in meters per second
            if (location.hasSpeed()) {
                val speed = location.speed * 3.6 // Convert to km/h
                binding.speedTextView.text = "%.0f".format(speed)
            } else {
                Log.d("Speed", "Speed data not available")
            }
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            10, // Update every 1 second
            0f,   // Update whenever there's a location change
            locationListener
        )

        checkPermission()
    }

    private fun checkPermission() {
        // Request location updates
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                1f,
                locationListener
            )
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }
}
