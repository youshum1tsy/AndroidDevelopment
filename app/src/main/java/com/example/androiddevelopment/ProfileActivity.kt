package com.example.androiddevelopment

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val btnChangePassword = findViewById<Button>(R.id.btnChangePassword)
        btnChangePassword.setOnClickListener {
            android.widget.Toast.makeText(this, "Функция смены пароля в разработке", android.widget.Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
    }
}