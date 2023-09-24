package com.example.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMainFragmentHomeBinding
import com.example.socialmediaapp.databinding.ActivityMainFragmentHomeListItemBinding
import com.example.utils.Const
import com.example.utils.MyItemClickListener
import com.example.utils.PostsAdapter
import com.example.utils.SearchFragAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FragmentHome : Fragment(), MyItemClickListener {
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

        val adapter = PostsAdapter(users, requireContext(), this)
        binding.rv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rv.adapter = adapter

        val usersRef = mFireStore.collection(Const.FS_USERS)

        lifecycleScope.launch(Dispatchers.IO) {
            val updatedUsers: MutableList<QueryDocumentSnapshot> = mutableListOf()
            var size = 0

            usersRef.get()
                .addOnSuccessListener { data ->
                    for (userSnapshot in data) {
                        val user = userSnapshot.toObject(com.example.data.User::class.java)
                        if (user.posts.size > 0) {
                            size += user.posts.size
                            updatedUsers.add(userSnapshot)
                        }
                    }

                    // After the loop is done, update the adapter with the updatedUsers list
                    users.clear()  // Clear the old data
                    users.addAll(updatedUsers)  // Add the updated data
                    adapter.updateData(users, size)
                }
                .addOnFailureListener {
                    // Handle failure here
                }
        }

        return view
    }

    override fun onLikeButtonClickListener(binding: ActivityMainFragmentHomeListItemBinding) {
        // add the user id when like button is clicked in post's likes.
    }
}