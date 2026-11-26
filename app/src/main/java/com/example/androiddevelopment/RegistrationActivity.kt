package com.example.androiddevelopment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RegistrationActivity : AppCompatActivity() {
    private val TAG = "RegistrationActivity"
    private lateinit var db: DatabaseHandler

    private var avatarIndex = 0
    private val avatars = arrayOf(
        android.R.drawable.ic_menu_camera,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_myplaces
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        db = DatabaseHandler(this)

        val ivAvatar = findViewById<ImageView>(R.id.ivRegAvatar)
        val btnSelectAvatar = findViewById<Button>(R.id.btnSelectAvatar)
        val etLogin = findViewById<EditText>(R.id.etRegLogin)
        val etPassword = findViewById<EditText>(R.id.etRegPassword)
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etFullName = findViewById<EditText>(R.id.etRegFullName)
        val etBirthDate = findViewById<EditText>(R.id.etRegBirthDate)
        val rgGender = findViewById<RadioGroup>(R.id.rgGender)
        val btnSubmitReg = findViewById<Button>(R.id.btnSubmitReg)

        btnSelectAvatar.setOnClickListener {
            avatarIndex = (avatarIndex + 1) % avatars.size
            ivAvatar.setImageResource(avatars[avatarIndex])
        }

        ivAvatar.setOnClickListener {
            avatarIndex = (avatarIndex + 1) % avatars.size
            ivAvatar.setImageResource(avatars[avatarIndex])
        }

        btnSubmitReg.setOnClickListener {
            val login = etLogin.text.toString().trim()
            val password = etPassword.text.toString()
            val email = etEmail.text.toString().trim()
            val fullName = etFullName.text.toString().trim()
            val birthDate = etBirthDate.text.toString().trim()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Логин и пароль обязательны", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedId = rgGender.checkedRadioButtonId
            val gender = if (selectedId != -1) {
                findViewById<RadioButton>(selectedId).text.toString()
            } else {
                "М"
            }

            val willBeAdmin = db.getUsersCount() == 0
            val newUser = User(
                login = login,
                pass = password,
                email = email,
                fullName = fullName,
                birthDate = birthDate,
                gender = gender,
                avatarIndex = avatarIndex,
                isAdmin = willBeAdmin
            )

            if (db.addUser(newUser)) {
                val msg = if (willBeAdmin) {
                    "Вы зарегистрированы как Администратор!"
                } else {
                    "Пользователь зарегистрирован!"
                }
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Зарегистрирован: $login, isAdmin=$willBeAdmin")

                if (isTaskRoot) {
                    startActivity(Intent(this, HelloActivity::class.java))
                }
                finish()
            } else {
                Toast.makeText(this, "Такой логин уже существует", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}