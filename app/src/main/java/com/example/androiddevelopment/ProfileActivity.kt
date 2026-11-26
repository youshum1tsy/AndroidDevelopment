package com.example.androiddevelopment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var db: DatabaseHandler

    private val avatars = arrayOf(
        android.R.drawable.ic_menu_camera,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_myplaces
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        db = DatabaseHandler(this)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val loggedInUserId = prefs.getInt("current_user_id", -1)
        val loggedInUser = db.getUserById(loggedInUserId)

        val viewUserId = intent.getIntExtra("VIEW_USER_ID", loggedInUserId)
        var viewedUser = db.getUserById(viewUserId)

        val imgAvatar = findViewById<ImageView>(R.id.imgAvatar)
        val tvName = findViewById<TextView>(R.id.tvProfileName)
        val tvEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val tvDetails = findViewById<TextView>(R.id.tvProfileDetails)
        val btnMakeAdmin = findViewById<Button>(R.id.btnMakeAdmin)
        val btnChangePassword = findViewById<Button>(R.id.btnChangePassword)
        val btnBack = findViewById<Button>(R.id.btnBack)

        if (viewedUser != null) {
            val safeIndex = viewedUser.avatarIndex.coerceIn(0, avatars.lastIndex)
            imgAvatar.setImageResource(avatars[safeIndex])

            tvName.text = viewedUser.fullName.ifEmpty { viewedUser.login }
            tvEmail.text = viewedUser.email.ifEmpty { "Логин: ${viewedUser.login}" }

            fun updateDetails(user: User) {
                val role = if (user.isAdmin) "Администратор" else "Обычный пользователь"
                tvDetails.text = "Логин: ${user.login}\nДата рождения: ${user.birthDate.ifEmpty { "-" }}\nПол: ${user.gender}\nРоль: $role"
            }
            updateDetails(viewedUser)

            // Кнопка "Сделать администратором" видна только админу, если просматривается обычный пользователь
            if (loggedInUser?.isAdmin == true && !viewedUser.isAdmin) {
                btnMakeAdmin.visibility = View.VISIBLE
            }

            btnMakeAdmin.setOnClickListener {
                db.makeAdmin(viewedUser!!.id)
                viewedUser = db.getUserById(viewedUser!!.id)
                viewedUser?.let { updateDetails(it) }
                btnMakeAdmin.visibility = View.GONE
                Toast.makeText(this, "Пользователь назначен администратором!", Toast.LENGTH_SHORT).show()
            }
        }

        btnChangePassword.setOnClickListener {
            Toast.makeText(this, "Функция смены пароля", Toast.LENGTH_SHORT).show()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}