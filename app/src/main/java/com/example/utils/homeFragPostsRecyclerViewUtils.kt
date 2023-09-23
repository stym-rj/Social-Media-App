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
    private var users: List<QueryDocumentSnapshot>,
    private val context: Context
): Adapter<PostsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val binding = ActivityMainFragmentHomeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        var size = 0
        for (user in users) {
            user.toObject(User::class.java).let {
                size += it.posts.size
            }
        }
        return size
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        Log.d("check3", position.toString())
        users[position].toObject(User::class.java).let {
            for (post in it.posts) {
                Log.d("check2", it.toString())
                holder.bind(it, post, context)
            }
        }
    }

    fun updateData(newList: MutableList<QueryDocumentSnapshot>) {
        users = newList
        notifyDataSetChanged()
    }
}