package com.example.prakticabd.models

import kotlinx.serialization.Serializable

@Serializable
data class Room(
    val id: String? = null,
    val name: String,
    val type: String,
    val userId: String
)
