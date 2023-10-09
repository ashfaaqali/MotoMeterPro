package com.ashfaq.motometerpro

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.ashfaq.motometerpro.databinding.ActivityMainBinding
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appUpdate: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appUpdate = AppUpdateManagerFactory.create(this)
        checkUpdate()

        // Check if location permission is already granted
        if (isLocationPermissionGranted()) {
            navigateToSpeedActivity()
        }

        binding.givePermissionBtn.setOnClickListener {
            requestLocationPermission()
        }

        binding.settingsBtn.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
            finish()
        }
    }
    private val listener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            appUpdate.completeUpdate()
        }
    }
    private fun checkUpdate() {
        appUpdate.appUpdateInfo.addOnSuccessListener { updateInfo ->
            if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                if (updateInfo.updatePriority() >= 4 && updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    appUpdate.startUpdateFlowForResult(
                        updateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        9
                    )
                } else if (updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    appUpdate.startUpdateFlowForResult(
                        updateInfo,
                        AppUpdateType.FLEXIBLE,
                        this,
                        9
                    )
                    appUpdate.registerListener(listener)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateInProgress()
    }

    private fun updateInProgress() {
        appUpdate.appUpdateInfo.addOnSuccessListener { updateInfo ->
            if (updateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdate.startUpdateFlowForResult(
                    updateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    9
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        appUpdate.unregisterListener(listener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navigateToSpeedActivity()
            } else {
                binding.errorLayout.visibility = View.VISIBLE
                binding.askPermissionLayout.visibility = View.GONE
            }
        }
    }
    
    private fun isLocationPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun navigateToSpeedActivity() {
        val intent = Intent(applicationContext, Speed::class.java)
        startActivity(intent)
        finish()
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            0

        )
    }
}