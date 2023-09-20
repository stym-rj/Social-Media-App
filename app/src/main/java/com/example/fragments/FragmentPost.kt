package com.example.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMainFragmentPostBinding

class FragmentPost : Fragment() {

    private val binding by lazy {
        ActivityMainFragmentPostBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = binding.root





        return view

    }
}