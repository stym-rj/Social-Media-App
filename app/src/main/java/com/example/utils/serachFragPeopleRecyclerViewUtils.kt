package com.example.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMainFragmentSearchPeopleItemsBinding
import com.google.firebase.firestore.auth.User

class SearchFragViewHolder (
    private val binding: ActivityMainFragmentSearchPeopleItemsBinding
): ViewHolder(binding.root) {
    fun bind (user: com.example.data.User, context: Context) {
        binding.tvName.text = user.fullName
        binding.tvEmail.text = user.email
        if (user.profilePic == "") {
            Glide
                .with(context)
                .load(R.mipmap.default_profile_pic_2)
                .into(binding.ivProfilepic)
        } else {
            Glide
                .with(context)
                .load(user.profilePic)
                .into(binding.ivProfilepic)
        }
    }
}

class SearchFragAdapter (
    private var users: List<com.example.data.User>,
    private val context: Context
): Adapter<SearchFragViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchFragViewHolder {
        val binding = ActivityMainFragmentSearchPeopleItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchFragViewHolder(binding)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: SearchFragViewHolder, position: Int) {
        holder.bind(users[position], context)
    }

    fun updateData(newList: MutableList<com.example.data.User>) {
        users = newList
        notifyDataSetChanged()
    }

}