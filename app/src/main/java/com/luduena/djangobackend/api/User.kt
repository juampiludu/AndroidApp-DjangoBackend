package com.luduena.djangobackend.api

class User (
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val token: String
)