package com.example.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.data.Post
import com.example.socialmediaapp.databinding.ActivityMainFragmentProfilePostsItemsBinding
import com.google.firebase.firestore.DocumentSnapshot

class ProfileFragPostViewHolder (
    private val binding: ActivityMainFragmentProfilePostsItemsBinding
) : ViewHolder(binding.root){
    fun bind(uri: String, user: DocumentSnapshot, position: Int, listener: PostClickListener, context: Context) {
        Glide
            .with(context)
            .load(uri)
            .into(binding.ivImage)

        itemView.setOnClickListener {
            listener.onPostClickListener(user, position)
        }
    }
}

class ProfileFragPostAdapter (
    private val posts: List<Post>,
    private val user: DocumentSnapshot,
    private val listener: PostClickListener,
    private val context: Context
) : Adapter<ProfileFragPostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileFragPostViewHolder {
        val binding = ActivityMainFragmentProfilePostsItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileFragPostViewHolder(binding)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ProfileFragPostViewHolder, position: Int) {
        holder.bind(posts[position].imageUrl, user, position, listener, context)
    }

}

interface PostClickListener {
    fun onPostClickListener(user: DocumentSnapshot, position: Int)
}