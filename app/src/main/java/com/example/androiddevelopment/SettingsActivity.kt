package com.example.androiddevelopment

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val switchTheme = findViewById<Switch>(R.id.switchTheme)
        val switchDbDelay = findViewById<Switch>(R.id.switchDbDelay)
        val btnBack = findViewById<Button>(R.id.btnSettingsBack)

        switchTheme.isChecked = prefs.getBoolean("is_dark_theme", false)
        switchDbDelay.isChecked = prefs.getBoolean("db_delay_enabled", true)

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("is_dark_theme", isChecked).apply()
            val mode = if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        switchDbDelay.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("db_delay_enabled", isChecked).apply()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}