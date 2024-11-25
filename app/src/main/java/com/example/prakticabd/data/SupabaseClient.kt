package com.example.prakticabd.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import com.example.prakticabd.models.User
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
            // Получаем user_id по email
            val userId = getUserIdByEmail(email)
            
            // Проверяем, существует ли уже адрес для этого пользователя
            val existingAddress = client.postgrest["user_addresses"]
                .select(Columns.raw("id")) {
                    eq("user_id", userId)
                }
                .decodeList<JsonObject>()

            val addressData = buildJsonObject {
                put("user_id", userId)
                put("address", address)
            }

            if (existingAddress.isEmpty()) {
                // Если адреса нет, создаем новый
                client.postgrest["user_addresses"].insert(addressData)
            } else {
                // Если адрес есть, обновляем его
                client.postgrest["user_addresses"]
                    .update(addressData) {
                        eq("user_id", userId)
                    }
            }
        } catch (e: Exception) {
            throw Exception("Ошибка при сохранении адреса: ${e.message}")
        }
    }

    suspend fun getUserAddress(email: String): String? {
        return try {
            val userId = getUserIdByEmail(email)
            val response = client.postgrest["user_addresses"]
                .select(Columns.raw("address")) {
                    eq("user_id", userId)
                }
                .decodeList<JsonObject>()
            
            if (response.isNotEmpty()) {
                response[0]["address"]?.jsonPrimitive?.content
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getUserIdByEmail(email: String): String {
        try {
            val response = client.postgrest["users"]
                .select(Columns.raw("id")) {
                    eq("email", email)
                }
                .decodeList<JsonObject>()
            
            if (response.isEmpty()) {
                throw Exception("Пользователь с email $email не найден")
            }
            
            return response[0]["id"]?.jsonPrimitive?.content 
                ?: throw Exception("ID пользователя не найден")
        } catch (e: Exception) {
            throw Exception("Ошибка при получении ID пользователя: ${e.message}")
        }
    }
}
