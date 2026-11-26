package com.example.androiddevelopment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class UserListActivity : AppCompatActivity() {
    private val TAG = "UserListActivity"
    private lateinit var db: DatabaseHandler
    private lateinit var lvUsers: ListView
    private lateinit var btnAddUser: Button
    private val usersList = ArrayList<User>()
    private lateinit var usersAdapter: ArrayAdapter<User>

    private val avatars = arrayOf(
        android.R.drawable.ic_menu_camera,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_myplaces
    )

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (isFinishing || isDestroyed) return

            btnAddUser.isEnabled = true
            btnAddUser.setText(R.string.btn_add_user)

            @Suppress("UNCHECKED_CAST")
            val loadedUsers = msg.obj as? ArrayList<User> ?: return
            usersList.clear()
            usersList.addAll(loadedUsers)
            usersAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        db = DatabaseHandler.getInstance(this)
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val currentUserId = prefs.getInt("current_user_id", -1)

        lvUsers = findViewById(R.id.lvUsers)
        btnAddUser = findViewById(R.id.btnAddUser)
        val btnUserBack = findViewById<Button>(R.id.btnUserBack)

        usersAdapter = object : ArrayAdapter<User>(
            this,
            R.layout.user_item,
            R.id.tvUserName,
            usersList
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val user = usersList[position]

                val ivAvatar = view.findViewById<ImageView>(R.id.ivUserAvatar)
                val tvName = view.findViewById<TextView>(R.id.tvUserName)
                val btnDelete = view.findViewById<ImageButton>(R.id.btnDeleteUser)

                val displayName = user.fullName.ifEmpty { user.login }
                val adminTag = if (user.isAdmin) " [Админ]" else ""
                tvName.text = "$displayName$adminTag"

                val safeIndex = user.avatarIndex.coerceIn(0, avatars.lastIndex)
                ivAvatar.setImageResource(avatars[safeIndex])

                val bitmap = DatabaseHandler.base64ToBitmap(user.avatarPhoto)
                if (bitmap != null) {
                    ivAvatar.setImageBitmap(bitmap)
                } else {
                    val safeIndex = user.avatarIndex.coerceIn(0, avatars.lastIndex)
                    ivAvatar.setImageResource(avatars[safeIndex])
                }

                view.setOnClickListener {
                    val intent = Intent(this@UserListActivity, ProfileActivity::class.java)
                    intent.putExtra("VIEW_USER_ID", user.id)
                    startActivity(intent)
                }

                btnDelete.setOnClickListener {
                    if (user.id == currentUserId) {
                        Toast.makeText(this@UserListActivity, "Нельзя удалить самого себя", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    btnDelete.isEnabled = false
                    Thread {
                        db.deleteUser(user.id)
                        Log.d(TAG, "Удалён пользователь: ${user.login}")
                        val updated = db.getAllUsers()
                        val msg = Message.obtain()
                        msg.obj = updated
                        handler.sendMessage(msg)
                    }.start()
                }

                return view
            }
        }

        lvUsers.adapter = usersAdapter

        btnAddUser.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        btnUserBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadUsersAsync()
    }

    private fun loadUsersAsync() {
        btnAddUser.isEnabled = false
        btnAddUser.text = "Загрузка..."
        Thread {
            val loaded = db.getAllUsers()
            val msg = Message.obtain()
            msg.obj = loaded
            handler.sendMessage(msg)
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}