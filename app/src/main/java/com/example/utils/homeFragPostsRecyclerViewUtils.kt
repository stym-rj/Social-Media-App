package com.example.utils

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.data.Post
import com.example.data.User
import com.example.socialmediaapp.databinding.ActivityMainFragmentHomeListItemBinding
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostsViewHolder (
    private val binding: ActivityMainFragmentHomeListItemBinding
): ViewHolder(binding.root) {
    fun bind(user: User, post: Post, context: Context) {
        Log.d("check1", user.toString())
            binding.tvName.text = user.fullName
            Glide
                .with(context)
                .load(user.profilePic)
                .into(binding.ivProfilePic)
            Glide
                .with(context)
                .load(post.imageUrl)
                .into(binding.ivPost)
            binding.tvNoOfLikes.text = "${post.likes.size} Likes"
            binding.tvCaption.text = post.caption
    }
}

class PostsAdapter(
    private var users: List<User>,
    private val context: Context,
    private var size: Int = 0,
    private var userIndex: Int = 0,
    private var postIndex: Int = 0
): Adapter<PostsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val binding = ActivityMainFragmentHomeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostsViewHolder(binding)
    }

    override fun getItemCount() = size

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        holder.bind(users[userIndex], users[userIndex].posts[postIndex], context)
        postIndex++
        if (postIndex >= users[userIndex].posts.size) {
            postIndex = 0
            userIndex++
        }
    }

    fun updateData(newList: MutableList<User>, newSize: Int) {
        users = newList
        size = newSize
        notifyDataSetChanged()
    }
}