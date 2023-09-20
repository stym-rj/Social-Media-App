package com.example.data

data class Post(
    val imageUrl: String = "",
    val caption: String = "",
    val likes: MutableList<String> = mutableListOf()
)