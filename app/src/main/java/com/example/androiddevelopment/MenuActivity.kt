package com.example.androiddevelopment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    private lateinit var db: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        db = DatabaseHandler(this)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val currentUserId = prefs.getInt("current_user_id", -1)
        val currentUser = db.getUserById(currentUserId)

        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnProfile = findViewById<Button>(R.id.btnProfile)
        val btnAdmin = findViewById<Button>(R.id.btnAdmin)
        val btnSettings = findViewById<Button>(R.id.btnSettings)
        val btnProblem = findViewById<Button>(R.id.btnProblem)
        val btnExit = findViewById<Button>(R.id.btnExit)

        // Кнопка "Администрирование" видна только администратору
        btnAdmin.visibility = if (currentUser?.isAdmin == true) View.VISIBLE else View.GONE

        btnStart.setOnClickListener {
            Toast.makeText(this, "Привет, ${currentUser?.login ?: "Гость"}!", Toast.LENGTH_SHORT).show()
        }

        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("VIEW_USER_ID", currentUserId)
            startActivity(intent)
        }

        btnAdmin.setOnClickListener {
            startActivity(Intent(this, UserListActivity::class.java))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnProblem.setOnClickListener {
            Toast.makeText(this, "Сообщение о проблеме отправлено", Toast.LENGTH_SHORT).show()
        }

        btnExit.setOnClickListener {
            prefs.edit().remove("current_user_id").apply()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}