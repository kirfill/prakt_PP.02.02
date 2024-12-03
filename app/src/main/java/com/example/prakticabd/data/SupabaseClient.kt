package com.example.prakticabd.data

import android.util.Log
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import io.github.jan.supabase.postgrest.query.Columns

object SupabaseClient {
    private const val SUPABASE_URL = "https://uygvbbyhxwnhwsfftlal.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV5Z3ZiYnloeHduaHdzZmZ0bGFsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzIwODk3NDIsImV4cCI6MjA0NzY2NTc0Mn0.FT9aDoU5TReEWg291aTEICmO9M_0ePUYXKGGVOPAHIM"

    private val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
    }

    suspend fun checkEmailExists(email: String): Boolean {
        val response = client.postgrest["users"]
            .select(Columns.raw("email")) {
                eq("email", email)
            }
            .decodeList<JsonObject>()
        return response.isNotEmpty()
    }

    suspend fun saveUserData(username: String, email: String, password: String) {
        val userData = buildJsonObject {
            put("username", username)
            put("email", email)
            put("password", password)
        }
        client.postgrest["users"].insert(userData)
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        val response = client.postgrest["users"]
            .select(Columns.raw("*")) {
                eq("email", email)
                eq("password", password)
            }
            .decodeList<JsonObject>()
        return response.isNotEmpty()
    }

    suspend fun saveUserAddress(email: String, address: String) {
        try {
            val addressData = buildJsonObject {
                put("address", address)
            }

            client.postgrest["users"]
                .update(addressData) {
                    eq("email", email)
                }
        } catch (e: Exception) {
            throw Exception("Ошибка при сохранении адреса: ${e.message}")
        }
    }

    suspend fun updateUserAddress(email: String, newAddress: String) {
        saveUserAddress(email, newAddress)
    }

    suspend fun getUserIdByEmail(email: String): String? {
        return try {
            val response = client.postgrest["users"]
                .select(Columns.raw("id")) {
                    eq("email", email)
                }
                .decodeSingle<JsonObject>()
            response["id"]?.jsonPrimitive?.content
        } catch (e: Exception) {
            Log.e("SupabaseClient", "Error getting user ID: ${e.message}")
            null
        }
    }

    suspend fun getUserAddress(email: String): String? {
        return try {
            val response = client.postgrest["users"]
                .select(Columns.raw("address")) {
                    eq("email", email)
                }
                .decodeList<JsonObject>()
            response.firstOrNull()?.get("address")?.jsonPrimitive?.content
        } catch (e: Exception) {
            Log.e("SupabaseClient", "Error getting user address", e)
            null
        }
    }

    suspend fun saveRoom(name: String, typeId: String, userId: String) {
        val roomData = buildJsonObject {
            put("name", name)
            put("type_id", typeId.toInt())
            put("user_id", userId)
        }
        Log.d("SupabaseClient", "Saving room with data: $roomData")
        client.postgrest["rooms"].insert(roomData)
    }

    fun getRoomTypeName(typeId: Int): String {
        return when (typeId) {
            1 -> "Гостиная"
            2 -> "Кухня"
            3 -> "Ванная"
            4 -> "Кабинет"
            5 -> "Спальня"
            6 -> "Зал"
            else -> "Неизвестный тип"
        }
    }
}

data class Room(val id: String, val name: String, val typeId: Int)
