package com.example.foregroundservicetest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var serviceSwitch: SwitchCompat
    private lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101
                )
            }
        }

        serviceSwitch = findViewById(R.id.serviceSwitch)
        sharedPreferences = getSharedPreferences("ServicePrefs", Context.MODE_PRIVATE)

        // Restore switch state
        val isServiceRunning = sharedPreferences.getBoolean("serviceRunning", false)
        serviceSwitch.isChecked = isServiceRunning

        // Set up the switch to start/stop the service
        serviceSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Start the foreground service
                val serviceIntent = Intent(this, MyForegroundService::class.java)
                if (Build.VERSION.SDK_INT >= 26) startForegroundService(serviceIntent)
                else startService(serviceIntent)
            } else {
                // Stop the foreground service
                val serviceIntent = Intent(this, MyForegroundService::class.java)
                stopService(serviceIntent)
            }
            // Save switch state
            sharedPreferences.edit().putBoolean("serviceRunning", isChecked).apply()
        }
    }
}