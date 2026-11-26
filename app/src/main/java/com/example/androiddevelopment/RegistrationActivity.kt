package com.example.androiddevelopment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class RegistrationActivity : AppCompatActivity() {
    private val TAG = "RegistrationActivity"
    private lateinit var db: DatabaseHandler
    private lateinit var btnSubmitReg: Button
    private lateinit var ivAvatar: ImageView

    private var avatarIndex = 0
    private var capturedPhotoBase64 = ""

    private val avatars = arrayOf(
        android.R.drawable.ic_menu_camera,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_myplaces
    )

    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            ivAvatar.setImageBitmap(bitmap)
            capturedPhotoBase64 = DatabaseHandler.bitmapToBase64(bitmap)
            Log.d(TAG, "Фото с камеры установлено как аватар")
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            openCamera()
        } else {
            Toast.makeText(this, "Разрешение на камеру не выдано", Toast.LENGTH_SHORT).show()
        }
    }

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

        ivAvatar = findViewById(R.id.ivRegAvatar)
        val btnSelectAvatar = findViewById<Button>(R.id.btnSelectAvatar)
        val btnTakePhoto = findViewById<Button>(R.id.btnTakePhoto)
        val etLogin = findViewById<EditText>(R.id.etRegLogin)
        val etPassword = findViewById<EditText>(R.id.etRegPassword)
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etFullName = findViewById<EditText>(R.id.etRegFullName)
        val etBirthDate = findViewById<EditText>(R.id.etRegBirthDate)
        val rgGender = findViewById<RadioGroup>(R.id.rgGender)
        btnSubmitReg = findViewById(R.id.btnSubmitReg)

        btnSelectAvatar.setOnClickListener {
            capturedPhotoBase64 = ""
            avatarIndex = (avatarIndex + 1) % avatars.size
            ivAvatar.setImageResource(avatars[avatarIndex])
        }

        ivAvatar.setOnClickListener {
            capturedPhotoBase64 = ""
            avatarIndex = (avatarIndex + 1) % avatars.size
            ivAvatar.setImageResource(avatars[avatarIndex])
        }

        btnTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
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
            val currentPhoto = capturedPhotoBase64
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
                    avatarPhoto = currentPhoto,
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

    private fun openCamera() {
        try {
            takePhotoLauncher.launch(null)
        } catch (e: Exception) {
            Toast.makeText(this, "Камера недоступна", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}