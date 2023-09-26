package com.example.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.data.User
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMainFragmentProfileBinding
import com.example.socialmediaapp.databinding.ActivityMainFragmentProfileEditProfileBottomSheetBinding
import com.example.socialmediaapp.databinding.ActivityMainFragmentProfileFollowersBottomSheetBinding
import com.example.socialmediaapp.databinding.ActivityMainFragmentProfileFollowingsBottomSheetBinding
import com.example.utils.Const
import com.example.utils.FollowButtonClickListener
import com.example.utils.PostClickListener
import com.example.utils.ProfileFragPostAdapter
import com.example.utils.SearchFragAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.FieldPosition
import java.util.UUID

class FragmentProfile : Fragment(), FollowButtonClickListener, PostClickListener {

    private val binding by lazy {
        ActivityMainFragmentProfileBinding.inflate(layoutInflater)
    }

    private lateinit var editBottomSheetBinding: ActivityMainFragmentProfileEditProfileBottomSheetBinding

    private lateinit var followersBottomSheetBinding: ActivityMainFragmentProfileFollowersBottomSheetBinding
    private lateinit var followingBottomSheetBinding: ActivityMainFragmentProfileFollowingsBottomSheetBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var mFireStore: FirebaseFirestore
    private val mFirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private lateinit var userReference: DocumentReference
    private lateinit var imageReference: StorageReference
    private lateinit var currentUser: User

    // image picker functionality
    private var chosenProfilePic: Uri? = null
    private val profileImagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        chosenProfilePic = uri
        chosenProfilePic?.let {
            setBottomSheetProfilePic(chosenProfilePic!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = binding.root

        // references from firebase
        mFireStore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        imageReference = mFirebaseStorage.reference.child(Const.STORAGE_PROFILE_PIC)

        binding.btnEdit.setOnClickListener {
            showEditBottomSheet(binding)
        }

        binding.tvFollowersSize.setOnClickListener {
            showFollowersBottomSheet()
        }

        binding.tvFollowingsSize.setOnClickListener {
            showFollowingBottomSheet()
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userReference = mFireStore.collection(Const.FS_USERS).document(auth.currentUser?.uid ?: "")
        lifecycleScope.launch(Dispatchers.IO) {
            userReference.get()
                .addOnSuccessListener { data ->
                    data.toObject(User::class.java)?.let { usr ->
                        currentUser = usr
                        setFragmentDetails()
                        // setting up the recycler view
                        val adapter = ProfileFragPostAdapter(currentUser.posts, data, this@FragmentProfile, requireContext())
                        binding.rv.layoutManager = GridLayoutManager(
                            requireContext(),
                            3,
                            RecyclerView.VERTICAL,
                            false
                        )
                        binding.rv.adapter = adapter
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun showFollowersBottomSheet() {
        followersBottomSheetBinding = ActivityMainFragmentProfileFollowersBottomSheetBinding.inflate(layoutInflater, null, false)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(followersBottomSheetBinding.root)

        dialog.show()

        getBottomSheetList(Const.FS_USER_FOLLOWERS, followersBottomSheetBinding.rv)

    }

    private fun showFollowingBottomSheet() {
        followingBottomSheetBinding = ActivityMainFragmentProfileFollowingsBottomSheetBinding.inflate(layoutInflater, null, false)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(followingBottomSheetBinding.root)

        dialog.show()

        getBottomSheetList(Const.FS_USER_FOLLOWING, followingBottomSheetBinding.rv)
    }

    private fun getBottomSheetList(parameter: String, rv: RecyclerView) {
        val usersReference = mFireStore.collection(Const.FS_USERS).document(auth.currentUser?.uid ?: "")

        var users: MutableList<QueryDocumentSnapshot> = mutableListOf()

        // adapter
        val adapter = SearchFragAdapter(users, auth.currentUser?.uid!!, requireContext(), this)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)


        CoroutineScope(Dispatchers.IO).launch {
            val followers = usersReference.get().await()
            val followersListRef = followers.get(parameter) as? List<String>

            if (followersListRef != null) {
                val updatedUsers: MutableList<QueryDocumentSnapshot> = mutableListOf()
                val followerRef = mFireStore
                    .collection(Const.FS_USERS)
                    .whereIn(FieldPath.documentId(), followersListRef)
                    .get().await()
                for (follower in followerRef) {
                    updatedUsers.add(follower)
                }
                withContext(Dispatchers.Main) {
                    users.clear()
                    users.addAll(updatedUsers)
                    adapter.updateData(users)
                }
            }
        }
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

    private fun setBottomSheetProfilePic(chosenProfilePic: Uri) {
        editBottomSheetBinding.ivEditProfilePic.setImageURI(chosenProfilePic)
    }


    private fun showEditBottomSheet(binding: ActivityMainFragmentProfileBinding) {
        editBottomSheetBinding = ActivityMainFragmentProfileEditProfileBottomSheetBinding.inflate(layoutInflater, null, false)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(editBottomSheetBinding.root)

        dialog.show()

        editBottomSheetBinding.etEditName.setText(binding.tvName.text)
        editBottomSheetBinding.etEditAbout.setText(binding.tvAbout.text)


        editBottomSheetBinding.ivEditProfilePic.setOnClickListener {
            // launching intent for picking image from gallery
            profileImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        editBottomSheetBinding.btnDone.setOnClickListener {
            // TODO: edit name, profile-pic and about and update this on fire-store.
            currentUser.fullName = editBottomSheetBinding.etEditName.text.toString()
            currentUser.about = editBottomSheetBinding.etEditAbout.text.toString()
            userReference.set(currentUser)
            chosenProfilePic?.let {
                updateProfilePic(it)
            }
            setFragmentDetails()


            dialog.dismiss()
        }
    }


    private fun setFragmentDetails() {
        if (currentUser.profilePic.isNotBlank()) {
            // load to iv
            Glide.with(this).load(currentUser.profilePic).into(binding.ivProfilePic)
        }
        binding.tvName.text = currentUser.fullName
        binding.tvAbout.text = currentUser.about
        binding.tvFollowersSize.text = currentUser.followers.size.toString()
        binding.tvFollowingsSize.text = currentUser.followings.size.toString()
        binding.tvPostSize.text = currentUser.posts.size.toString()
    }

    private fun updateProfilePic (uri: Uri) {
        val ref = imageReference.child(UUID.randomUUID().toString())
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { result ->
                    currentUser.profilePic = result.toString()
                    userReference.set(currentUser)
                    Glide.with(this).load(result.toString()).into(binding.ivProfilePic)
                }
            }
            .addOnFailureListener {

            }
    }

    override fun onPostClickListener(user: DocumentSnapshot, position: Int) {
        val fragment = FragmentProfileFullViewPost.newInstance(user.id, position)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}