package com.example.androiddevelopment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    private lateinit var db: DatabaseHandler
    private lateinit var btnAdmin: Button
    private var currentUser: User? = null

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (isFinishing || isDestroyed) return
            currentUser = msg.obj as? User
            btnAdmin.visibility = if (currentUser?.isAdmin == true) View.VISIBLE else View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        db = DatabaseHandler.getInstance(this)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val currentUserId = prefs.getInt("current_user_id", -1)

        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnProfile = findViewById<Button>(R.id.btnProfile)
        btnAdmin = findViewById(R.id.btnAdmin)
        val btnSettings = findViewById<Button>(R.id.btnSettings)
        val btnProblem = findViewById<Button>(R.id.btnProblem)
        val btnExit = findViewById<Button>(R.id.btnExit)

        // Загружаем данные пользователя в отдельном потоке
        Thread {
            val user = db.getUserById(currentUserId)
            val msg = Message.obtain()
            msg.obj = user
            handler.sendMessage(msg)
        }.start()

        btnStart.setOnClickListener {
            Toast.makeText(this, "Привет, ${currentUser?.login ?: "Пользователь"}!", Toast.LENGTH_SHORT).show()
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
        handler.removeCallbacksAndMessages(null)
    }
}