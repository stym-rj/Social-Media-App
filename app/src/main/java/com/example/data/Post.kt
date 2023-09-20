package com.example.data

data class Post(
    val imageUrl: String = "",
    val caption: String = "",
    val createdAt: Long = 0L,
    val likes: MutableList<String> = mutableListOf()
)