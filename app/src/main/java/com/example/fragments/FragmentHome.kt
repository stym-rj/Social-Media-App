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
import com.example.data.IndividualPost
import com.example.data.Post
import com.example.data.User
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMainFragmentHomeBinding
import com.example.socialmediaapp.databinding.ActivityMainFragmentHomeListItemBinding
import com.example.utils.Const
import com.example.utils.MyItemClickListener
import com.example.utils.PostsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FragmentHome : Fragment(), MyItemClickListener {
    private val binding by lazy {
        ActivityMainFragmentHomeBinding.inflate(layoutInflater)
    }
    private lateinit var mFireStore: FirebaseFirestore
    private var users: MutableList<DocumentSnapshot> = mutableListOf()
    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = binding.root

        mFireStore = FirebaseFirestore.getInstance()

        val posts: MutableList<Post> = mutableListOf()

        val adapter = PostsAdapter(users, posts, mAuth.currentUser?.uid ?: "", requireContext(), this)
        binding.rv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rv.adapter = adapter

        // fire-store user's reference
        val usersRef = mFireStore.collection(Const.FS_USERS)

        // fireStore post's reference
        val postRef = mFireStore.collection(Const.FS_POSTS)

        lifecycleScope.launch(Dispatchers.IO) {

            postRef.get()
                .addOnSuccessListener { data ->
                    val updatedUsers: MutableList<DocumentSnapshot> = mutableListOf()
                    val updatedPosts: MutableList<Post> = mutableListOf()

                    for (postSnapshot in data) {
                        postSnapshot.toObject(IndividualPost::class.java).let { postData ->
                            updatedPosts.add(postData.post)
                            Log.d("check100", postData.userRef)
                            CoroutineScope(Dispatchers.IO).launch {
                                val userRequest = usersRef.document(postData.userRef).get().await()
                                updatedUsers.add(userRequest)
                                users.clear()
                                posts.clear()
                                users.addAll(updatedUsers)
                                posts.addAll(updatedPosts)
                                withContext(Dispatchers.Main) {
                                    adapter.updateData(users, posts)
                                }
                            }
                        }

                    }
                }
                .addOnFailureListener {

                }
        }

        return view
    }

    override fun onLikeButtonClickListener(
        userRef: DocumentSnapshot,
        indexOfPost: Int,
        binding: ActivityMainFragmentHomeListItemBinding
    ) {
        // add the user id when like button is clicked in post's likes.
        CoroutineScope(Dispatchers.IO).launch {
            val result = mFireStore.collection(Const.FS_USERS).document(userRef.id).get().await()

            result.toObject(User::class.java)?.let { data ->
                    if (data.posts[indexOfPost].likes.contains(mAuth.currentUser?.uid)) {
                        withContext(Dispatchers.Main) {
                            binding.ivLike.setImageResource(R.drawable.ic_like_normal)
                            binding.tvNoOfLikes.text = "${data.posts[indexOfPost].likes.size - 1} Likes"
                        }
                        data.posts[indexOfPost].likes.remove(mAuth.currentUser?.uid)
                    } else {
                        data.posts[indexOfPost].likes.add(mAuth.currentUser?.uid ?: "a")
                        withContext(Dispatchers.Main) {
                            binding.ivLike.setImageResource(R.drawable.ic_like_clicked)
                            binding.tvNoOfLikes.text = "${data.posts[indexOfPost].likes.size} Likes"
                        }
                    }

                val updatePost = IndividualPost(post = data.posts[indexOfPost], userRef = userRef.id)
                mFireStore.collection(Const.FS_POSTS).document(data.posts[indexOfPost].createdAt.toString())
                    .set(updatePost).await()

                mFireStore.collection(Const.FS_USERS).document(userRef.id)
                    .set(data).await()
            }
        }
    }
}