package com.example.androiddevelopment

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class HelloActivity : AppCompatActivity() {
    private val TAG = "HelloActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helloact)

        val textList = findViewById<ListView>(R.id.textList)
        val button1 = findViewById<Button>(R.id.button1)

        val myStringArray = ArrayList<String>()
        val textAdapter = ArrayAdapter(this, R.layout.item, R.id.itemContent, myStringArray)
        textList.adapter = textAdapter

        button1.setOnClickListener {
            val item = "Element_${myStringArray.size}"
            myStringArray.add(item)
            textAdapter.notifyDataSetChanged()
            Log.d(TAG, "Добавлен элемент: $item")
        }
    }
}