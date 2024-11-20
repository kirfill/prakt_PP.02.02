package com.example.prakticabd

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.regex.Pattern

class RegActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reg)
        val button: Button = findViewById(R.id.button4)
        val submitButton: Button = findViewById(R.id.submitButton)
        val emailEditText: EditText = findViewById(R.id.emailEditText)



        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        submitButton.setOnClickListener {
            val email = emailEditText.text.toString()
            if (isValidEmail(email)) {
                Toast.makeText(this, "Valid Email", Toast.LENGTH_SHORT).show()
                // Переход к следующей активности или другое действие
            } else {
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        val pattern = Pattern.compile(emailRegex)
        return pattern.matcher(email).matches()
    }

        }

