package com.example.androiddevelopment

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class RegistrationActivity : AppCompatActivity() {
    private val TAG = "RegistrationActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        Log.d(TAG, "onCreate: Активность регистрации создана")

        if (savedInstanceState != null) {
            findViewById<EditText>(R.id.etRegLogin).setText(savedInstanceState.getString("login"))
            findViewById<EditText>(R.id.etRegEmail).setText(savedInstanceState.getString("email"))
            findViewById<EditText>(R.id.etRegPassword).setText(savedInstanceState.getString("pass"))
        }

        findViewById<Button>(R.id.btnSubmitReg).setOnClickListener {
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("login", findViewById<EditText>(R.id.etRegLogin).text.toString())
        outState.putString("email", findViewById<EditText>(R.id.etRegEmail).text.toString())
        outState.putString("pass", findViewById<EditText>(R.id.etRegPassword).text.toString())
        Log.d(TAG, "onSaveInstanceState: Данные регистрации сохранены")
    }

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume") }
    override fun onPause() { super.onPause(); Log.d(TAG, "onPause") }
    override fun onStop() { super.onStop(); Log.d(TAG, "onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy") }
}