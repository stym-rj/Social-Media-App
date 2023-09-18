package com.example

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.fragments.FragmentHome
import com.example.fragments.FragmentPost
import com.example.fragments.FragmentProfile
import com.example.fragments.FragmentSearch
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        // bottom nav switching frags
        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.botNavHome -> setFragContainer(FragmentHome())
                R.id.botNavSearch -> setFragContainer(FragmentSearch())
                R.id.botNavPost -> setFragContainer(FragmentPost())
                R.id.botNavProfile -> setFragContainer(FragmentProfile())
                else -> setFragContainer(FragmentHome())
            }
            true
        }

        // chat intent
        binding.appToolbar.ivMessageIcon.setOnClickListener {
            Intent(this, ChatActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun setFragContainer(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, fragment)
            .commit()
    }
}