package com.example.authFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.MainActivity
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityAuthenticationFragmentSignInBinding

class FragmentSignIn : Fragment() {

    private lateinit var bindingFragSignIn: ActivityAuthenticationFragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_authentication_fragment_sign_in, container, false)


//            bindingFragSignIn.btnSignIn.setOnClickListener {
//                val intent = Intent(activity, MainActivity::class.java).also {
//                    startActivity(it)
//                }
//            }

        val btnSwitchActivity = view?.findViewById<Button>(R.id.btnSignIn)

        btnSwitchActivity?.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}