package com.example.androiddevelopment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class HelloActivity : AppCompatActivity() {
    private val TAG = "HelloActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello)
        Log.d(TAG, "onCreate: Активность создана")
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
        if (savedInstanceState != null) {
            val savedText = savedInstanceState.getString("key_login")
            val editText = findViewById<EditText>(R.id.etLogin)
            editText.setText(savedText)
            Log.d(TAG, "onCreate: Данные восстановлены")
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Активность становится видимой")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Активность готова к взаимодействию")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Активность приостановлена")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Активность остановлена")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Активность уничтожена")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val editText = findViewById<EditText>(R.id.etLogin)
        outState.putString("key_login", editText.text.toString())

        Log.d(TAG, "onSaveInstanceState: Данные сохранены")
    }
}