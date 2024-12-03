package com.example.prakticabd

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.prakticabd.data.SupabaseClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var backToRegisterButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Инит
        emailEditText = findViewById(R.id.loginEmailEditText)
        passwordEditText = findViewById(R.id.loginPasswordEditText)
        loginButton = findViewById(R.id.loginButton)
        backToRegisterButton = findViewById(R.id.backToRegisterButton)

        // кнопки входа
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Пожалуйста, заполните все поля")
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        // возврата к регистрации
        backToRegisterButton.setOnClickListener {
            finish()
        }
    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val isValid = SupabaseClient.loginUser(email, password)
                if (isValid) {
                    Log.d(TAG, "Вход успешен, сохраняем email")
                    
                    // Сохраняем email
                    saveUserEmail(email)
                    
                    // Переходим на главный экран
                    val intent = Intent(this@LoginActivity, MainScreen::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showToast("Неверный email или пароль")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при входе", e)
                showToast("Ошибка входа: ${e.message}")
            }
        }
    }

    private fun saveUserEmail(email: String) {
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("USER_EMAIL", email)
            apply()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
