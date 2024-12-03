package com.example.prakticabd

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)

        Log.d(TAG, "onCreate started")

        try {
            // Инициализация views
            usernameEditText = findViewById(R.id.Username)
            emailEditText = findViewById(R.id.emailEditText)
            passwordEditText = findViewById(R.id.password)
            
            // Находим кнопки
            val saveButton: Button = findViewById(R.id.saveButton)
            val loginButton: Button = findViewById(R.id.loginButton)
            
            Log.d(TAG, "Views initialized")

            // Обработчик для кнопки регистрации
            saveButton.setOnClickListener { view ->
                Log.d(TAG, "Save button clicked")
                Toast.makeText(this, "Кнопка нажата", Toast.LENGTH_SHORT).show()
                
                val username = usernameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (validateInput(username, email, password)) {
                    if (isInternetAvailable()) {
                        checkEmailExistsAndRegisterUser(username, email, password)
                    } else {
                        Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_LONG).show()
                    }
                }
            }

            // Обработчик для кнопки входа
            loginButton.setOnClickListener { view ->
                Log.d(TAG, "Login button clicked")
                try {
                    val tempEmail = "test@example.com"
                    saveUserEmail(tempEmail)
                    
                    val intent = Intent(this, MainScreen::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка при переходе на MainScreen", e)
                    showToast("Ошибка: ${e.message}")
                }
            }
            
            Log.d(TAG, "Listeners set")
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            Toast.makeText(this, "Ошибка инициализации: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateInput(username: String, email: String, password: String): Boolean {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Пожалуйста, заполните все поля")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Пожалуйста, введите корректный email")
            return false
        }

        return true
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun checkEmailExistsAndRegisterUser(username: String, email: String, password: String) {
        lifecycleScope.launch {
            try {
                // Проверяем существует ли пользователь с таким email
                val exists = SupabaseClient.checkEmailExists(email)
                if (exists) {
                    Toast.makeText(this@RegActivity, "Пользователь с таким email уже существует", Toast.LENGTH_LONG).show()
                    return@launch
                }

                // Если email не существует, регистрируем нового пользователя
                registerUser(username, email, password)
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при регистрации", e)
                showToast("Ошибка регистрации: ${e.message}")
            }
        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Начало регистрации пользователя...")
                SupabaseClient.saveUserData(username, email, password)
                Log.d(TAG, "Регистрация успешна, сохраняем email")
                
                saveUserEmail(email)
                
                Log.d(TAG, "Email сохранен, переход к экрану ввода адреса")
                
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
