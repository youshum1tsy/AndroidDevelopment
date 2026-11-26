package com.example.androiddevelopment

data class User(
    val id: Int = -1,
    val login: String,
    val pass: String,
    val email: String = "",
    val fullName: String = "",
    val birthDate: String = "",
    val gender: String = "М",
    val avatarIndex: Int = 0,
    val avatarPhoto: String = "",
    val isAdmin: Boolean = false
)