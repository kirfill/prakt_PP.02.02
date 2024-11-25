package com.example.prakticabd

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.Intent
import android.os.Handler
import android.os.Looper


class splashakrivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splashakrivity)


        // Задержка в 10 секунд
        Handler(Looper.getMainLooper()).postDelayed({
            // Запуск MainActivity
            startActivity(Intent(this, RegActivity::class.java))
            // Завершение SplashScreenActivity
            finish()
        }, 5000)
        }
    }
