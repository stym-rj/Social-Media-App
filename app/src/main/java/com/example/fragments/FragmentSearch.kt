package com.example.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.data.User
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMainFragmentSearchBinding
import com.example.utils.Const
import com.example.utils.SearchFragAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FragmentSearch : Fragment() {

    private val binding by lazy {
        ActivityMainFragmentSearchBinding.inflate(layoutInflater)
    }

    private lateinit var mFireStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var users: MutableList<User> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()

        val usersReference = mFireStore.collection(Const.FS_USERS)

        // adapter
        val adapter = SearchFragAdapter(users, requireContext())
        binding.rv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rv.adapter = adapter

        lifecycleScope.launch (Dispatchers.IO){
            usersReference.get()
                .addOnSuccessListener { data ->
                    for (user in data) {
                        if (user.id == usersReference.document(auth.currentUser?.uid!!).id) {
                            continue
                        }
                        users.add(user.toObject(User::class.java))

                        Log.d("check", user.toObject(User::class.java).toString())
                    }
                    adapter.updateData(users)
                }
                .addOnFailureListener {

                }
        }


        binding.svSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?) = false

            override fun onQueryTextChange(p0: String?): Boolean {
                CoroutineScope(Dispatchers.IO).launch {
                    if (p0.isNullOrBlank()) {
                        adapter.updateData(users)
                    } else {
                        val filteredUsers = users.filter {
                            it.fullName.contains(p0, ignoreCase = true)
                        }
                        withContext(Dispatchers.Main) {
                            adapter.updateData(filteredUsers.toMutableList())
                        }
                    }
                }

                return true
            }

        })

        return view
    }

    private fun updateAdapter(filteredUsers: List<User>, adapter: SearchFragAdapter) {
        users.clear()
        users.addAll(filteredUsers)

        adapter.notifyDataSetChanged()
    }
}