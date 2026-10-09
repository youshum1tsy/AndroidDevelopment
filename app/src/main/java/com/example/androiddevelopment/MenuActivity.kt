package com.example.androiddevelopment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViewById<Button>(R.id.btnProfile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<Button>(R.id.btnProblem).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+70000000000")
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnExit).setOnClickListener {
            val intent = Intent(this, HelloActivity::class.java)
            val editText = findViewById<EditText>(R.id.etTask)
            intent.putExtra("TEXT_TASK", editText.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }
}