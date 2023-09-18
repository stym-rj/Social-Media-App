package com.example.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMainFragmentProfileBinding

class FragmentProfile : Fragment() {

    private val binding by lazy {
        ActivityMainFragmentProfileBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {






        return binding.root


    }
}