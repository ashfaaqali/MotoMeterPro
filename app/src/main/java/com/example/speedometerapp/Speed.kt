package com.example.speedometerapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.speedometerapp.databinding.ActivitySpeedBinding

class Speed : AppCompatActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    private lateinit var binding: ActivitySpeedBinding

    private lateinit var fineLocation: String
    private var granted = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        granted = PackageManager.PERMISSION_GRANTED

        // Initialize location manager and listener
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGpsProviderEnabled) {
            binding.speedTextView.visibility = View.GONE
            binding.kmhTv.visibility = View.GONE
            binding.gps.visibility = View.VISIBLE

            turnOnGPS()
        }
        requestLocationUpdates()
    }

    private fun turnOnGPS(){
        Toast.makeText(applicationContext, "Please turn on Location", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
        finish()
    }
    private fun requestLocationUpdates() {
        locationListener = LocationListener { location ->
            // Calculate speed in meters per second
            if (location.hasSpeed()) {
                val speed = location.speed * 3.6 // Convert to km/h
                binding.speedTextView.text = "%.0f".format(speed)
            } else {
                Log.d("Speed", "Speed data not available")
            }
        }

        if (ActivityCompat.checkSelfPermission(this, fineLocation) == granted) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0, // Update every 1 second
                0f,   // Update whenever there's a location change
                locationListener
            )
        }
    }
}