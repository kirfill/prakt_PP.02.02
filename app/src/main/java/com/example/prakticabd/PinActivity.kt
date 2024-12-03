package com.example.prakticabd

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class PinActivity : AppCompatActivity() {
    private val pinDots = mutableListOf<View>()
    private val enteredPin = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pin)

        // Инициализация точек PIN-кода
        pinDots.add(findViewById(R.id.pinDot1))
        pinDots.add(findViewById(R.id.pinDot2))
        pinDots.add(findViewById(R.id.pinDot3))
        pinDots.add(findViewById(R.id.pinDot4))

        // Инициализация кнопок с цифрами
        setupNumericButton(R.id.buttonPIN1, "1")
        setupNumericButton(R.id.buttonPIN2, "2")
        setupNumericButton(R.id.buttonPIN3, "3")
        setupNumericButton(R.id.buttonPIN4, "4")
        setupNumericButton(R.id.buttonPIN5, "5")
        setupNumericButton(R.id.buttonPIN6, "6")
        setupNumericButton(R.id.buttonPIN7, "7")
        setupNumericButton(R.id.buttonPIN8, "8")
        setupNumericButton(R.id.buttonPIN9, "9")
    }

    private fun setupNumericButton(buttonId: Int, number: String) {
        findViewById<Button>(buttonId).setOnClickListener {
            if (enteredPin.length < 4) {
                enteredPin.append(number)
                updatePinDots()
                
                // Проверяем длина 4
                if (enteredPin.length == 4) {
                    savePin(enteredPin.toString())
                }
            }
        }
    }

    private fun updatePinDots() {
        for (i in pinDots.indices) {
            pinDots[i].setBackgroundResource(
                if (i < enteredPin.length) R.drawable.pin_dot_filled
                else R.drawable.pin_dot_empty
            )
        }
    }

    private fun clearPin() {
        enteredPin.clear()
        updatePinDots()
    }

    private fun savePin(pin: String) {
        // Сохраняем PIN
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("PIN_CODE", pin)
            apply()
        }

        // Показываем сообщение
        Toast.makeText(this, "PIN-код успешно сохранен", Toast.LENGTH_SHORT).show()

        // Переходим на MainScreen
        startActivity(Intent(this, MainScreen::class.java))
        finish()
    }
}
