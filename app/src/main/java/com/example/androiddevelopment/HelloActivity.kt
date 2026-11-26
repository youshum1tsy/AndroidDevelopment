package com.example.androiddevelopment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class HelloActivity : AppCompatActivity() {
    private val TAG = "HelloActivity"
    private lateinit var db: DatabaseHandler
    private lateinit var btnLogin: Button

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (isFinishing || isDestroyed) return

            btnLogin.isEnabled = true
            btnLogin.setText(R.string.btn_login)

            val user = msg.obj as? User
            if (user != null) {
                getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .edit()
                    .putInt("current_user_id", user.id)
                    .apply()

                val role = if (user.isAdmin) "Администратор" else "Пользователь"
                Toast.makeText(this@HelloActivity, "Вход выполнен ($role)", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Успешный вход: ${user.login}, isAdmin=${user.isAdmin}")

                startActivity(Intent(this@HelloActivity, MenuActivity::class.java))
            } else {
                Toast.makeText(this@HelloActivity, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Ошибка входа")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isDark = prefs.getBoolean("is_dark_theme", false)
        val targetMode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
            AppCompatDelegate.setDefaultNightMode(targetMode)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello)

        db = DatabaseHandler.getInstance(this)

        val etLogin = findViewById<EditText>(R.id.etLogin)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnExit = findViewById<Button>(R.id.btnExit)

        btnLogin.setOnClickListener {
            val login = etLogin.text.toString().trim()
            val pass = etPassword.text.toString()

            if (login.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Введите логин и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            btnLogin.text = "Вход..."

            Thread {
                val user = db.loginUser(login, pass)
                val msg = Message.obtain()
                msg.obj = user
                handler.sendMessage(msg)
            }.start()
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
        handler.removeCallbacksAndMessages(null)
    }
}