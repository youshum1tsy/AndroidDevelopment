package com.example.androiddevelopment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class HelloActivity : AppCompatActivity() {
    private val TAG = "HelloActivity"
    private lateinit var db: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        // Применяем тему из SharedPreferences до отрисовки экрана
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isDark = prefs.getBoolean("is_dark_theme", false)
        val targetMode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
            AppCompatDelegate.setDefaultNightMode(targetMode)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello)

        db = DatabaseHandler(this)

        val etLogin = findViewById<EditText>(R.id.etLogin)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnExit = findViewById<Button>(R.id.btnExit)

        btnLogin.setOnClickListener {
            val login = etLogin.text.toString().trim()
            val pass = etPassword.text.toString()

            if (login.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Введите логин и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = db.loginUser(login, pass)
            if (user != null) {
                prefs.edit().putInt("current_user_id", user.id).apply()
                val role = if (user.isAdmin) "Администратор" else "Пользователь"
                Toast.makeText(this, "Вход выполнен ($role)", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Успешный вход: ${user.login}, isAdmin=${user.isAdmin}")

                startActivity(Intent(this, MenuActivity::class.java))
            } else {
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Ошибка входа для логина: $login")
            }
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        btnExit.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}