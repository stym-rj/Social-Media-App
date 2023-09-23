package com.example.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMainFragmentHomeBinding
import com.example.utils.Const
import com.example.utils.PostsAdapter
import com.example.utils.SearchFragAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentHome : Fragment() {
    private val binding by lazy {
        ActivityMainFragmentHomeBinding.inflate(layoutInflater)
    }
    private lateinit var mFireStore: FirebaseFirestore
    private var users: MutableList<QueryDocumentSnapshot> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = binding.root

        mFireStore = FirebaseFirestore.getInstance()

        val adapter = PostsAdapter(users, requireContext())
        binding.rv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rv.adapter = adapter

        val usersRef = mFireStore.collection(Const.FS_USERS)

        lifecycleScope.launch(Dispatchers.IO) {
            usersRef.get()
                .addOnSuccessListener { data ->
                    for (user in data) {
                        user.toObject(com.example.data.User::class.java).let {
                            if (it.posts.size > 0) {
                                users.add(user)
                            }
                        }
                    }
                    adapter.updateData(users)
                }
                .addOnFailureListener {

                }
        }

        return view


    }
}