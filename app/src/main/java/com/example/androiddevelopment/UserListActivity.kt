package com.example.androiddevelopment

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class UserListActivity : AppCompatActivity() {
    private val TAG = "UserListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        val lvUsers = findViewById<ListView>(R.id.lvUsers)
        val btnAddUser = findViewById<Button>(R.id.btnAddUser)

        val usersList = ArrayList<String>()
        usersList.add("Балаев Глеб Сергеевич")
        usersList.add("Ларин Егор Дмитриевич")

        val usersAdapter = ArrayAdapter(this, R.layout.item, R.id.itemContent, usersList)
        lvUsers.adapter = usersAdapter

        btnAddUser.setOnClickListener {
            val newUser = "Пользователь ${usersList.size + 1}"
            usersList.add(newUser)
            usersAdapter.notifyDataSetChanged()
            Log.d(TAG, "Нажата кнопка Добавить -> добавлен $newUser")
        }
    }
}