package com.luduena.djangobackend.api

class Comment (
    val id: Int,
    val post: Int,
    val author: String,
    val content: String,
    val published_date: String
)