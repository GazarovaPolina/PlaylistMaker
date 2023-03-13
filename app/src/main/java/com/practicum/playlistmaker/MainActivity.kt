package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSearch = findViewById<Button>(R.id.btnSearch)

        val btnSearchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку \"Поиск!\"", Toast.LENGTH_SHORT).show()
            }
        }

        btnSearch.setOnClickListener(btnSearchClickListener)

        val btnMediaLib = findViewById<Button>(R.id.btnMediaLib)

        btnMediaLib.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку \"Медиатека!\"", Toast.LENGTH_SHORT).show()
        }

        val btnSettings = findViewById<Button>(R.id.btnSettings)

        btnSettings.setOnClickListener {
            Toast.makeText(this@MainActivity,  "Нажали на кнопку \"Настройки!\"", Toast.LENGTH_SHORT).show()
        }
    }
}