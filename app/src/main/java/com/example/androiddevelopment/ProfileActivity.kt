package com.example.androiddevelopment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var db: DatabaseHandler

    private lateinit var imgAvatar: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvDetails: TextView
    private lateinit var btnMakeAdmin: Button

    private var loggedInUser: User? = null
    private var viewedUser: User? = null

    private val avatars = arrayOf(
        android.R.drawable.ic_menu_camera,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_myplaces
    )

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (isFinishing || isDestroyed) return

            when (msg.what) {
                1 -> { // Загрузка профиля
                    @Suppress("UNCHECKED_CAST")
                    val pair = msg.obj as? Pair<User?, User?> ?: return
                    loggedInUser = pair.first
                    viewedUser = pair.second

                    viewedUser?.let { showUser(it) }
                }
                2 -> { // Назначение администратором
                    btnMakeAdmin.isEnabled = true
                    btnMakeAdmin.setText(R.string.btn_make_admin)
                    viewedUser = msg.obj as? User
                    viewedUser?.let { showUser(it) }
                    Toast.makeText(
                        this@ProfileActivity,
                        "Пользователь назначен администратором!",
                        Toast.LENGTH_SHORT
                    ).show()
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

    private fun showUser(user: User) {
        val safeIndex = user.avatarIndex.coerceIn(0, avatars.lastIndex)
        imgAvatar.setImageResource(avatars[safeIndex])
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