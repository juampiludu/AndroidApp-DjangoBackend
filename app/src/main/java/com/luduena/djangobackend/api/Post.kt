package com.luduena.djangobackend.api

class Post (
    var id: Int,
    val author: String,
    var title: String,
    var body_text: String,
    var published_date: String
)