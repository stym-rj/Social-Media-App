package com.example.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.data.User
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMainFragmentHomeListItemBinding
import com.example.socialmediaapp.databinding.ActivityMainFragmentProfileFullViewPostBinding
import com.example.utils.Const
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentProfileFullViewPost : Fragment() {

    private val binding by lazy {
        ActivityMainFragmentHomeListItemBinding.inflate(layoutInflater)
    }
    private lateinit var mFireStore: FirebaseFirestore

    private lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = binding.root

        val uid = arguments?.getString("uid")
        val position = arguments?.getInt("position")





        mFireStore = FirebaseFirestore.getInstance()
        val userRef = mFireStore.collection(Const.FS_USERS).document(uid!!)

        lifecycleScope.launch(Dispatchers.IO) {
            userRef.get()
                .addOnSuccessListener {
                    it.toObject(User::class.java)?.let { data ->
                        user = data
                        Glide
                            .with(requireContext())
                            .load(user.profilePic)
                            .into(binding.ivPost)
                        binding.tvName.text = user.fullName
                        Glide
                            .with(requireContext())
                            .load(user.posts[position!!].imageUrl)
                            .into(binding.ivPost)
                        binding.tvNoOfLikes.text = "${user.posts[position].likes.size.toString()} Likes"
                        binding.tvCaption.text = user.posts[position].caption
                    }
                }
        }

        return view
    }

    companion object {
        @JvmStatic fun newInstance(param1: String, param2: Int): FragmentProfileFullViewPost {
            val fragment = FragmentProfileFullViewPost()
            val bundle = Bundle()
            bundle.putString("uid", param1)
            bundle.putInt("position", param2)
            fragment.arguments = bundle
            return fragment
        }
    }
}