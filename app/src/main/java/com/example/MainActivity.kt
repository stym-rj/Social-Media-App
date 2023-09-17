package com.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.Fragments.FragmentHome
import com.example.Fragments.FragmentPost
import com.example.Fragments.FragmentProfile
import com.example.Fragments.FragmentSearch
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

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
    }

    private fun setFragContainer(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, fragment)
            .commit()
    }
}