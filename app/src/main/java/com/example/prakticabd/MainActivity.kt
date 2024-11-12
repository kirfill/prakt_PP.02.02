package com.example.prakticabd

import android.os.Bundle
import android.widget.Button
import android.content.Intent

import androidx.activity.enableEdgeToEdge

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
            }
        val button = findViewById<Button>(R.id.buttonregistr)
        button.setOnClickListener {
            button.setOnClickListener {
                // Создаем Intent для перехода на RegActivity
                val intent = Intent(this, RegActivity::class.java)

                // Запускаем активность
                startActivity(intent)

        }
    }