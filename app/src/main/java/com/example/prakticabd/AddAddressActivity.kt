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

class AddAddressActivity : AppCompatActivity() {
    private val TAG = "AddAddressActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        Log.d(TAG, "Activity created")

        val addressEditText = findViewById<EditText>(R.id.addressEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)

        // Получаем email пользователя из SharedPreferences
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("USER_EMAIL", null)
        
        Log.d(TAG, "Retrieved user email: $userEmail")

        // Если есть email, пробуем загрузить существующий адрес
        userEmail?.let { email ->
            Log.d(TAG, "Attempting to load existing address for email: $email")
            lifecycleScope.launch {
                try {
                    val savedAddress = SupabaseClient.getUserAddress(email)
                    Log.d(TAG, "Loaded address: $savedAddress")
                    savedAddress?.let {
                        addressEditText.setText(it)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading address", e)
                    Toast.makeText(this@AddAddressActivity, "Ошибка загрузки адреса: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Log.e(TAG, "No user email found in SharedPreferences")
            Toast.makeText(this, "Ошибка: email пользователя не найден", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, RegActivity::class.java))
            finish()
            return
        }

        saveButton.setOnClickListener {
            val address = addressEditText.text.toString().trim()
            Log.d(TAG, "Save button clicked with address: $address")

            if (address.isNotEmpty()) {
                if (isValidAddress(address)) {
                    Log.d(TAG, "Address validation passed, attempting to save")
                    lifecycleScope.launch {
                        try {
                            // Сохраняем адрес в Supabase
                            SupabaseClient.saveUserAddress(userEmail!!, address)
                            Log.d(TAG, "Address saved successfully")
                            
                            // Показываем сообщение об успехе
                            Toast.makeText(this@AddAddressActivity, "Адрес успешно сохранён", Toast.LENGTH_SHORT).show()
                            
                            // Переходим к экрану ввода PIN-кода
                            val intent = Intent(this@AddAddressActivity, PinActivity::class.java)
                            startActivity(intent)
                            finish()
                        } catch (e: Exception) {
                            Log.e(TAG, "Error saving address", e)
                            Toast.makeText(
                                this@AddAddressActivity,
                                "Ошибка сохранения адреса: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Log.d(TAG, "Address validation failed")
                    Toast.makeText(this, "Пожалуйста, введите полный адрес (город, улица, дом)", Toast.LENGTH_LONG).show()
                }
            } else {
                Log.d(TAG, "Empty address")
                Toast.makeText(this, "Введите адрес", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidAddress(address: String): Boolean {
        val lowercaseAddress = address.lowercase()
        val hasCity = lowercaseAddress.contains("г.") || lowercaseAddress.contains("город")
        val hasStreet = lowercaseAddress.contains("ул.") || lowercaseAddress.contains("улица")
        val hasHouse = lowercaseAddress.contains("д.") || lowercaseAddress.contains("дом")
        
        Log.d(TAG, "Address validation - hasCity: $hasCity, hasStreet: $hasStreet, hasHouse: $hasHouse")
        
        return hasCity && hasStreet && hasHouse
    }
}
