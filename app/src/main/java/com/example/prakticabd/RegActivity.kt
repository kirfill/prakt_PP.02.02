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

class RegActivity : AppCompatActivity() {
    private val TAG = "RegActivity"
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)

        // Инициализация views
        usernameEditText = findViewById(R.id.Username)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.password)
        registerButton = findViewById(R.id.button4)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateInput(username, email, password)) {
                registerUser(username, email, password)
            }
        }
    }

    private fun validateInput(username: String, email: String, password: String): Boolean {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Пожалуйста, заполните все поля")
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Пожалуйста, введите корректный email")
            return false
        }

        return true
    }

    private fun registerUser(username: String, email: String, password: String) {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Начало регистрации пользователя...")
                SupabaseClient.saveUserData(username, email, password)
                Log.d(TAG, "Регистрация успешна, сохраняем email")
                
                // Сохраняем email в SharedPreferences
                val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("USER_EMAIL", email)
                    apply()
                }
                
                Log.d(TAG, "Email сохранен, переход к экрану ввода адреса")
                
                // Переходим к экрану ввода адреса вместо PIN
                val intent = Intent(this@RegActivity, AddAddressActivity::class.java)
                startActivity(intent)
                Log.d(TAG, "Переход выполнен")
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при регистрации", e)
                showToast("Ошибка регистрации: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
