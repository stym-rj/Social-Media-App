package com.example.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMainFragmentSearchPeopleItemsBinding
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.auth.User

class SearchFragViewHolder (
    private val binding: ActivityMainFragmentSearchPeopleItemsBinding
): ViewHolder(binding.root) {
    fun bind (user: QueryDocumentSnapshot, currentUserID: String, context: Context, listener: FollowButtonClickListener) {
        user.toObject(com.example.data.User::class.java)?.let {
            binding.tvName.text = it.fullName
            binding.tvEmail.text = it.email
            if (it.profilePic == "") {
                Glide
                    .with(context)
                    .load(R.mipmap.default_profile_pic_2)
                    .into(binding.ivProfilepic)
            } else {
                Glide
                    .with(context)
                    .load(it.profilePic)
                    .into(binding.ivProfilepic)
            }

            // setting follow button according to followed/ Not followed
            var followed: Boolean
            if (it.followers.contains(currentUserID)) {
                binding.btnFollow.text = "Unfollow"
                followed = true
            } else {
                binding.btnFollow.text = "Follow"
                followed = false
            }

            binding.btnFollow.setOnClickListener {
                listener.onFollowButtonClicked(user, followed)
            }
        }
    }
}

class SearchFragAdapter (
    private var users: List<QueryDocumentSnapshot>,
    private var currentUserID: String,
    private val context: Context,
    private val listener: FollowButtonClickListener
): Adapter<SearchFragViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchFragViewHolder {
        val binding = ActivityMainFragmentSearchPeopleItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchFragViewHolder(binding)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: SearchFragViewHolder, position: Int) {
        holder.bind(users[position], currentUserID, context, listener)
    }

    fun updateData(newList: MutableList<QueryDocumentSnapshot>) {
        users = newList
        notifyDataSetChanged()
    }

}


interface FollowButtonClickListener {
    fun onFollowButtonClicked(user: QueryDocumentSnapshot, followed: Boolean)
}