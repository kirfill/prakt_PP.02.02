package com.example.prakticabd

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.prakticabd.data.SupabaseClient
import kotlinx.coroutines.launch

class RoomActivity : AppCompatActivity() {
    private val TAG = "RoomActivity"
    private lateinit var nameEdit: EditText
    private lateinit var roomTypeRadioGroup: RadioGroup
    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        nameEdit = findViewById(R.id.nameEdit)
        roomTypeRadioGroup = findViewById(R.id.roomTypeRadioGroup)
        
        // Получаем email
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        userEmail = sharedPref.getString("USER_EMAIL", "") ?: ""
        
        if (userEmail.isEmpty()) {
            Log.e(TAG, "Email пользователя не найден")
            Toast.makeText(this, "Ошибка: email пользователя не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }

    fun Back(view: View) {
        finish()
    }

    fun Save(view: View) {
        Log.d(TAG, "=== Save method started ===")
        val roomName = nameEdit.text.toString()
        Log.d(TAG, "Room name entered: $roomName")
        
        val selectedRadioButtonId = roomTypeRadioGroup.checkedRadioButtonId
        Log.d(TAG, "Selected radio button ID: $selectedRadioButtonId")

        if (roomName.isEmpty()) {
            Log.d(TAG, "Room name is empty")
            Toast.makeText(this, "Введите название комнаты", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedRadioButtonId == -1) {
            Log.d(TAG, "No room type selected")
            Toast.makeText(this, "Выберите тип комнаты", Toast.LENGTH_SHORT).show()
            return
        }

        // Получаем тип комнаты
        val typeId = when (selectedRadioButtonId) {
            R.id.livingRoomRadio -> "1" // Гостиная
            R.id.kitchenRadio -> "2"    // Кухня
            R.id.bathroomRadio -> "3"   // Ванная
            R.id.officeRadio -> "4"     // Кабинет
            R.id.bedroomRadio -> "5"    // Спальня
            R.id.hallRadio -> "6"       // Зал
            else -> return
        }
        Log.d(TAG, "Selected type ID: $typeId")
        Log.d(TAG, "User email: $userEmail")

        lifecycleScope.launch {
            try {
                Log.d(TAG, "Starting coroutine for room saving...")
                
                // Получаем ID пользователя по email
                val userId = SupabaseClient.getUserIdByEmail(userEmail)
                Log.d(TAG, "Retrieved user ID: $userId")
                
                if (userId == null) {
                    Log.e(TAG, "User ID is null for email: $userEmail")
                    Toast.makeText(this@RoomActivity, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Сохраняем комнату
                Log.d(TAG, "Attempting to save room to database...")
                SupabaseClient.saveRoom(roomName, typeId, userId)
                Log.d(TAG, "Room successfully saved")

                Toast.makeText(this@RoomActivity, "Комната успешно добавлена", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "Error saving room: ${e.message}", e)
                Toast.makeText(this@RoomActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
