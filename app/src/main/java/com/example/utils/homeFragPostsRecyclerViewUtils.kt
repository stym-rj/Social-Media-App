package com.example.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.data.Post
import com.example.data.User
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMainFragmentHomeListItemBinding
import com.google.firebase.firestore.DocumentSnapshot

class PostsViewHolder (
    private val binding: ActivityMainFragmentHomeListItemBinding
): ViewHolder(binding.root) {
    fun bind(user: DocumentSnapshot, post: Post, currUserID: String, context: Context, listener: MyItemClickListener) {
        user.toObject(User::class.java)?.let { data->
            if (data.profilePic == "") {
                binding.ivProfilePic.setImageResource(R.mipmap.default_profile_pic_2)
            } else {
                Glide
                    .with(context)
                    .load(data.profilePic)
                    .into(binding.ivProfilePic)
            }
            Glide
                .with(context)
                .load(post.imageUrl)
                .into(binding.ivPost)

            binding.tvName.text = data.fullName
            binding.tvNoOfLikes.text = "${post.likes.size} Likes"
            binding.tvCaption.text = post.caption

            // like button state
            if (post.likes.contains(currUserID)) {
                binding.ivLike.setImageResource(R.drawable.ic_like_clicked)
            } else {
                binding.ivLike.setImageResource(R.drawable.ic_like_normal)
            }

            // like button click listener
            binding.ivLike.setOnClickListener {
                listener.onLikeButtonClickListener(user, data.posts.indexOf(post), binding)
            }
        }
    }
}

class PostsAdapter(
    private var users: MutableList<DocumentSnapshot>,
    private var posts: MutableList<Post>,
    private val currUserID: String,
    private val context: Context,
    private val listener: MyItemClickListener,
): Adapter<PostsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val binding = ActivityMainFragmentHomeListItemBinding
                            .inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            )
        return PostsViewHolder(binding)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        holder.bind(users[position], posts[position], currUserID, context, listener)
    }

    fun updateData(newUsers: MutableList<DocumentSnapshot>, newPosts: MutableList<Post>) {
        users = newUsers
        posts = newPosts
        notifyDataSetChanged()
    }
}


interface MyItemClickListener {
    fun onLikeButtonClickListener(
        userRef: DocumentSnapshot,
        indexOfPost: Int,
        binding: ActivityMainFragmentHomeListItemBinding
    )
}