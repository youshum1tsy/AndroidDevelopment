package com.example.androiddevelopment

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class RegistrationActivity : AppCompatActivity() {
    private val TAG = "RegistrationActivity"

    private var avatarIndex = 0
    private val avatars = arrayOf(
        android.R.drawable.ic_menu_camera,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_myplaces
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        Log.d(TAG, "onCreate: Активность регистрации создана")

        val ivAvatar = findViewById<ImageView>(R.id.ivRegAvatar)
        val btnSelectAvatar = findViewById<Button>(R.id.btnSelectAvatar)
        val etLogin = findViewById<EditText>(R.id.etRegLogin)
        val etPassword = findViewById<EditText>(R.id.etRegPassword)
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etFullName = findViewById<EditText>(R.id.etRegFullName)
        val etBirthDate = findViewById<EditText>(R.id.etRegBirthDate)
        val rgGender = findViewById<RadioGroup>(R.id.rgGender)
        val btnSubmitReg = findViewById<Button>(R.id.btnSubmitReg)

        if (savedInstanceState != null) {
            etLogin.setText(savedInstanceState.getString("login"))
            etEmail.setText(savedInstanceState.getString("email"))
            etPassword.setText(savedInstanceState.getString("pass"))
            etFullName.setText(savedInstanceState.getString("fullname"))
            etBirthDate.setText(savedInstanceState.getString("birthdate"))
        }

        btnSelectAvatar.setOnClickListener {
            avatarIndex = (avatarIndex + 1) % avatars.size
            ivAvatar.setImageResource(avatars[avatarIndex])
            Log.d(TAG, "Нажата кнопка выбора аватара (индекс: $avatarIndex)")
        }

        btnSubmitReg.setOnClickListener {
            val selectedId = rgGender.checkedRadioButtonId
            val gender = if (selectedId != -1) {
                findViewById<RadioButton>(selectedId).text.toString()
            } else {
                "Не выбран"
            }

            Log.d(
                TAG,
                "Нажата кнопка регистрации -> " +
                        "Логин: ${etLogin.text}, " +
                        "Пароль: ${etPassword.text}, " +
                        "Email: ${etEmail.text}, " +
                        "ФИО: ${etFullName.text}, " +
                        "Дата рождения: ${etBirthDate.text}, " +
                        "Пол: $gender"
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("login", findViewById<EditText>(R.id.etRegLogin).text.toString())
        outState.putString("email", findViewById<EditText>(R.id.etRegEmail).text.toString())
        outState.putString("pass", findViewById<EditText>(R.id.etRegPassword).text.toString())
        outState.putString("fullname", findViewById<EditText>(R.id.etRegFullName).text.toString())
        outState.putString("birthdate", findViewById<EditText>(R.id.etRegBirthDate).text.toString())
        Log.d(TAG, "onSaveInstanceState: Данные регистрации сохранены")
    }
}