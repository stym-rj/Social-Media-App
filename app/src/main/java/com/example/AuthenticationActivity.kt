package com.example

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.authFragments.FragmentSignIn
import com.example.authFragments.FragmentSignUp
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityAuthenticationBinding
import com.example.socialmediaapp.databinding.ActivityAuthenticationFragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // for setting up icon's colors to its default.
        binding.bottomNav.itemIconTintList = null

        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.bottomNavSignIn -> setFrag(FragmentSignIn())
                R.id.bottomNavSignUp -> setFrag(FragmentSignUp())
                else -> setFrag(FragmentSignIn())
            }
            true
        }

        // sending current user UID to main activity
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setFrag(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, fragment)
            .commit()
    }
}