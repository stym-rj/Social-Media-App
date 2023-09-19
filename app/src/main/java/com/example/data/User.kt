package com.example.data

data class User (
    var fullName: String = "",
    val eMail: String = "",
    var about: String = "",
    var followings: MutableList<String> = mutableListOf(),
    var followers: MutableList<String> = mutableListOf(),
    var profilePic: String = "",
    val posts: MutableList<String> = mutableListOf()
)