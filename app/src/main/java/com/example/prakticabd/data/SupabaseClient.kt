package com.example.prakticabd.data

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.util.UUID

object SupabaseClient {
    private const val SUPABASE_URL = "https://uygvbbyhxwnhwsfftlal.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV5Z3ZiYnloeHduaHdzZmZ0bGFsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzIwODk3NDIsImV4cCI6MjA0NzY2NTc0Mn0.FT9aDoU5TReEWg291aTEICmO9M_0ePUYXKGGVOPAHIM"

    val client = createSupabaseClient(
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
        if (checkEmailExists(email)) {
            throw Exception("Пользователь с таким email уже существует")
        }
        
        val userData = buildJsonObject {
            put("username", username)
            put("email", email)
            put("password", password)
        }
        client.postgrest["users"].insert(userData)
    }

    suspend fun saveRoom(name: String, type: String, userId: String) {
        val roomData = buildJsonObject {
            put("id", UUID.randomUUID().toString())
            put("name", name)
            put("type_id", type)
            put("user_id", userId)
        }
        Log.d("SupabaseClient", "Attempting to save room with data: $roomData")
        client.postgrest["rooms"].insert(roomData)
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
            throw e
        }
    }

    suspend fun getUserAddress(email: String): String? {
        return try {
            val response = client.postgrest["users"]
                .select(Columns.raw("address")) {
                    eq("email", email)
                }
                .decodeSingle<JsonObject>()
            response["address"]?.jsonPrimitive?.content
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserIdByEmail(email: String): String? {
        return try {
            val response = client.postgrest["users"]
                .select(Columns.raw("id")) {
                    eq("email", email)
                }
                .decodeSingle<JsonObject>()
            response["id"]?.jsonPrimitive?.content?.replace("\"", "")
        } catch (e: Exception) {
            null
        }
    }
}
