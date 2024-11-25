package com.example.prakticabd

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.prakticabd.data.SupabaseClient
import kotlinx.coroutines.launch

class AddAddressActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        val addressEditText = findViewById<EditText>(R.id.addressEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)

        // Получаем email пользователя из SharedPreferences
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("USER_EMAIL", null)

        // Если есть email, пробуем загрузить существующий адрес
        userEmail?.let { email ->
            lifecycleScope.launch {
                try {
                    val savedAddress = SupabaseClient.getUserAddress(email)
                    savedAddress?.let {
                        addressEditText.setText(it)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@AddAddressActivity, "Ошибка загрузки адреса", Toast.LENGTH_SHORT).show()
                }
            }
        }

        saveButton.setOnClickListener {
            val address = addressEditText.text.toString().trim()
            if (address.isNotEmpty() && userEmail != null) {
                if (isValidAddress(address)) {
                    lifecycleScope.launch {
                        try {
                            // Сохраняем адрес в Supabase
                            SupabaseClient.saveUserAddress(userEmail, address)
                            
                            // Показываем сообщение об успехе
                            Toast.makeText(this@AddAddressActivity, "Адрес успешно сохранён", Toast.LENGTH_SHORT).show()
                            
                            // Переходим к экрану ввода PIN-кода
                            startActivity(Intent(this@AddAddressActivity, PinActivity::class.java))
                            finish()
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@AddAddressActivity,
                                "Ошибка сохранения адреса: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Пожалуйста, введите полный адрес (город, улица, дом)", Toast.LENGTH_LONG).show()
                }
            } else if (userEmail == null) {
                Toast.makeText(this, "Ошибка: email пользователя не найден", Toast.LENGTH_SHORT).show()
                // Возвращаемся к экрану регистрации
                startActivity(Intent(this, RegActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Введите адрес", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidAddress(address: String): Boolean {
        // Простая проверка наличия города, улицы и дома в адресе
        val lowercaseAddress = address.lowercase()
        return lowercaseAddress.contains("г.") && 
               (lowercaseAddress.contains("ул.") || lowercaseAddress.contains("улица")) && 
               (lowercaseAddress.contains("д.") || lowercaseAddress.contains("дом"))
    }
}
