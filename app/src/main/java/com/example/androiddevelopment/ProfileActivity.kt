package com.example.androiddevelopment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ProfileActivity : AppCompatActivity() {
    private lateinit var db: DatabaseHandler

    private lateinit var imgAvatar: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvDetails: TextView
    private lateinit var btnMakeAdmin: Button
    private lateinit var btnProfileCamera: Button

    private var loggedInUser: User? = null
    private var viewedUser: User? = null

    private val avatars = arrayOf(
        android.R.drawable.ic_menu_camera,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_myplaces
    )

    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        val targetId = viewedUser?.id ?: return@registerForActivityResult
        if (bitmap != null) {
            imgAvatar.setImageBitmap(bitmap)
            val base64 = DatabaseHandler.bitmapToBase64(bitmap)
            btnProfileCamera.isEnabled = false
            btnProfileCamera.text = "Сохранение..."

            Thread {
                db.updateUserAvatar(targetId, base64)
                val updated = db.getUserById(targetId)
                val msg = Message.obtain()
                msg.what = 3
                msg.obj = updated
                handler.sendMessage(msg)
            }.start()
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

            when (msg.what) {
                1 -> {
                    @Suppress("UNCHECKED_CAST")
                    val pair = msg.obj as? Pair<User?, User?> ?: return
                    loggedInUser = pair.first
                    viewedUser = pair.second
                    viewedUser?.let { showUser(it) }
                }
                2 -> {
                    btnMakeAdmin.isEnabled = true
                    btnMakeAdmin.setText(R.string.btn_make_admin)
                    viewedUser = msg.obj as? User
                    viewedUser?.let { showUser(it) }
                    Toast.makeText(this@ProfileActivity, "Пользователь назначен администратором!", Toast.LENGTH_SHORT).show()
                }
                3 -> {
                    btnProfileCamera.isEnabled = true
                    btnProfileCamera.text = "Снять аватар на камеру"
                    viewedUser = msg.obj as? User
                    viewedUser?.let { showUser(it) }
                    Toast.makeText(this@ProfileActivity, "Аватар обновлён!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        db = DatabaseHandler.getInstance(this)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val loggedInUserId = prefs.getInt("current_user_id", -1)
        val viewUserId = intent.getIntExtra("VIEW_USER_ID", loggedInUserId)

        imgAvatar = findViewById(R.id.imgAvatar)
        btnProfileCamera = findViewById(R.id.btnProfileCamera)
        tvName = findViewById(R.id.tvProfileName)
        tvEmail = findViewById(R.id.tvProfileEmail)
        tvDetails = findViewById(R.id.tvProfileDetails)
        btnMakeAdmin = findViewById(R.id.btnMakeAdmin)
        val btnChangePassword = findViewById<Button>(R.id.btnChangePassword)
        val btnBack = findViewById<Button>(R.id.btnBack)

        tvName.text = "Загрузка..."
        tvEmail.text = ""
        tvDetails.text = ""

        Thread {
            val current = db.getUserById(loggedInUserId)
            val viewed = db.getUserById(viewUserId)
            val msg = Message.obtain()
            msg.what = 1
            msg.obj = Pair(current, viewed)
            handler.sendMessage(msg)
        }.start()

        btnProfileCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        btnMakeAdmin.setOnClickListener {
            val targetId = viewedUser?.id ?: return@setOnClickListener
            btnMakeAdmin.isEnabled = false
            btnMakeAdmin.text = "Назначение..."

            Thread {
                db.makeAdmin(targetId)
                val updated = db.getUserById(targetId)
                val msg = Message.obtain()
                msg.what = 2
                msg.obj = updated
                handler.sendMessage(msg)
            }.start()
        }

        btnChangePassword.setOnClickListener {
            Toast.makeText(this, "Функция смены пароля", Toast.LENGTH_SHORT).show()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun openCamera() {
        try {
            takePhotoLauncher.launch(null)
        } catch (e: Exception) {
            Toast.makeText(this, "Камера недоступна", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showUser(user: User) {
        val bitmap = DatabaseHandler.base64ToBitmap(user.avatarPhoto)
        if (bitmap != null) {
            imgAvatar.setImageBitmap(bitmap)
        } else {
            val safeIndex = user.avatarIndex.coerceIn(0, avatars.lastIndex)
            imgAvatar.setImageResource(avatars[safeIndex])
        }

        tvName.text = user.fullName.ifEmpty { user.login }
        tvEmail.text = user.email.ifEmpty { "Логин: ${user.login}" }

        val role = if (user.isAdmin) "Администратор" else "Обычный пользователь"
        tvDetails.text = "Логин: ${user.login}\nДата рождения: ${user.birthDate.ifEmpty { "-" }}\nПол: ${user.gender}\nРоль: $role"

        btnMakeAdmin.visibility = if (loggedInUser?.isAdmin == true && !user.isAdmin) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}