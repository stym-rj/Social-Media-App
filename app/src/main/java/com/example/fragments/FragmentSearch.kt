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
import com.example.MainActivity
import com.example.data.User
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMainFragmentSearchBinding
import com.example.utils.Const
import com.example.utils.FollowButtonClickListener
import com.example.utils.SearchFragAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FragmentSearch : Fragment(), FollowButtonClickListener {

    private val binding by lazy {
        ActivityMainFragmentSearchBinding.inflate(layoutInflater)
    }

    private lateinit var mFireStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var users: MutableList<QueryDocumentSnapshot> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()

        val usersReference = mFireStore.collection(Const.FS_USERS)

        // adapter
        val adapter = SearchFragAdapter(users, auth.currentUser?.uid!!, requireContext(), this)
        binding.rv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rv.adapter = adapter

        lifecycleScope.launch (Dispatchers.IO){
            usersReference.get()
                .addOnSuccessListener { data ->
                    for (user in data) {
                        if (user.id == auth.currentUser?.uid!!) {
                            continue
                        }
//                        users.add(user.toObject(User::class.java))
                        users.add(user)

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
                        val filteredUsers = users.filter { user ->
                            user.toObject(User::class.java).fullName.contains(p0, ignoreCase = true)
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

    override fun onFollowButtonClicked(user: QueryDocumentSnapshot, followed: Boolean) {
        if (followed) {
            auth = FirebaseAuth.getInstance()
            val usersReference = mFireStore.collection(Const.FS_USERS).document(auth.currentUser?.uid!!)
            usersReference.get()
                .addOnSuccessListener { data ->
                    // removing from current user's followings list
                    data.toObject(User::class.java)?.let {
                        it.followings.remove(user.id)
                        usersReference.set(it)
                    }

                    // removing from followed user's followers list
                    user.toObject(User::class.java).let {
                        it.followers.remove(auth.currentUser?.uid!!)
                        mFireStore.collection(Const.FS_USERS).document(user.id)
                            .set(it)
                    }
                }
        } else {
            auth = FirebaseAuth.getInstance()
            val usersReference = mFireStore.collection(Const.FS_USERS).document(auth.currentUser?.uid!!)
            usersReference.get()
                .addOnSuccessListener { data ->
                    // adding to current user's followings list
                    data.toObject(User::class.java)?.let {
                        it.followings.add(user.id)
                        usersReference.set(it)
                    }

                    // adding to followed user's followers list
                    user.toObject(User::class.java).let {
                        it.followers.add(auth.currentUser?.uid!!)
                        mFireStore.collection(Const.FS_USERS).document(user.id)
                            .set(it)
                    }
                }
        }
    }
}