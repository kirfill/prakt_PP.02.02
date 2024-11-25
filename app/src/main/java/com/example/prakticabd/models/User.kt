package com.example.prakticabd.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String? = null,
    val username: String,
    val email: String,
    val password: String
)
