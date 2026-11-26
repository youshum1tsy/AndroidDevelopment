package com.example.androiddevelopment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RegistrationActivity : AppCompatActivity() {
    private val TAG = "RegistrationActivity"
    private lateinit var db: DatabaseHandler
    private lateinit var btnSubmitReg: Button

    private var avatarIndex = 0
    private val avatars = arrayOf(
        android.R.drawable.ic_menu_camera,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_myplaces
    )

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (isFinishing || isDestroyed) return

            btnSubmitReg.isEnabled = true
            btnSubmitReg.setText(R.string.btn_submit_reg)

            val success = msg.what == 1
            val isAdmin = msg.obj as? Boolean ?: false

            if (success) {
                val text = if (isAdmin) {
                    "Вы зарегистрированы как Администратор!"
                } else {
                    "Пользователь зарегистрирован!"
                }
                Toast.makeText(this@RegistrationActivity, text, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Регистрация успешна, isAdmin=$isAdmin")

                if (isTaskRoot) {
                    startActivity(Intent(this@RegistrationActivity, HelloActivity::class.java))
                }
                finish()
            } else {
                Toast.makeText(this@RegistrationActivity, "Такой логин уже существует", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        db = DatabaseHandler.getInstance(this)

        val ivAvatar = findViewById<ImageView>(R.id.ivRegAvatar)
        val btnSelectAvatar = findViewById<Button>(R.id.btnSelectAvatar)
        val etLogin = findViewById<EditText>(R.id.etRegLogin)
        val etPassword = findViewById<EditText>(R.id.etRegPassword)
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etFullName = findViewById<EditText>(R.id.etRegFullName)
        val etBirthDate = findViewById<EditText>(R.id.etRegBirthDate)
        val rgGender = findViewById<RadioGroup>(R.id.rgGender)
        btnSubmitReg = findViewById(R.id.btnSubmitReg)

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

            val currentAvatar = avatarIndex
            btnSubmitReg.isEnabled = false
            btnSubmitReg.text = "Сохранение..."

            Thread {
                val willBeAdmin = db.getUsersCount() == 0
                val newUser = User(
                    login = login,
                    pass = password,
                    email = email,
                    fullName = fullName,
                    birthDate = birthDate,
                    gender = gender,
                    avatarIndex = currentAvatar,
                    isAdmin = willBeAdmin
                )
                val added = db.addUser(newUser)

                val msg = Message.obtain()
                msg.what = if (added) 1 else 0
                msg.obj = willBeAdmin
                handler.sendMessage(msg)
            }.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}