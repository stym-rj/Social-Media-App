package com.example.data

data class User (
    val fullName: String,
    val eMail: String,
    val about: String = "",
    var followings: MutableList<String> = mutableListOf(),
    var followers: MutableList<String> = mutableListOf(),
    val profilePic: String = "",
    val posts: MutableList<String> = mutableListOf()
)