package com.example.prakticabd

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainScreen : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userEmail: String
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        
        // Получаем email из SharedPreferences
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        userEmail = sharedPref.getString("USER_EMAIL", "") ?: ""
        
        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Ошибка: email пользователя не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        recyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // TODO: Установить адаптер для RecyclerView
        // recyclerView.adapter = YourAdapter()
    }
    
    fun Profile(view: View) {
        // TODO: Реализовать переход в профиль
    }
    
    fun Add(view: View) {
        val intent = Intent(this, RoomActivity::class.java)
        intent.putExtra("user_email", userEmail)
        startActivity(intent)
    }
}
