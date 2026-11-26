package com.example.androiddevelopment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class UserListActivity : AppCompatActivity() {
    private val TAG = "UserListActivity"
    private lateinit var db: DatabaseHandler
    private lateinit var lvUsers: ListView
    private val usersList = ArrayList<User>()
    private lateinit var usersAdapter: ArrayAdapter<User>

    private val avatars = arrayOf(
        android.R.drawable.ic_menu_camera,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_myplaces
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        db = DatabaseHandler(this)
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val currentUserId = prefs.getInt("current_user_id", -1)

        lvUsers = findViewById(R.id.lvUsers)
        val btnAddUser = findViewById<Button>(R.id.btnAddUser)
        val btnUserBack = findViewById<Button>(R.id.btnUserBack)
        btnUserBack.setOnClickListener {
            finish()
        }

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

                // Нажатие на саму строку -> открывает Профиль этого пользователя
                view.setOnClickListener {
                    val intent = Intent(this@UserListActivity, ProfileActivity::class.java)
                    intent.putExtra("VIEW_USER_ID", user.id)
                    startActivity(intent)
                }

                // Нажатие на крестик -> удаляет пользователя
                btnDelete.setOnClickListener {
                    if (user.id == currentUserId) {
                        Toast.makeText(this@UserListActivity, "Нельзя удалить самого себя", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    db.deleteUser(user.id)
                    loadUsers()
                    Log.d(TAG, "Удалён пользователь: ${user.login}")
                }

                return view
            }
        }

        lvUsers.adapter = usersAdapter

        btnAddUser.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadUsers()
    }

    private fun loadUsers() {
        usersList.clear()
        usersList.addAll(db.getAllUsers())
        usersAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}