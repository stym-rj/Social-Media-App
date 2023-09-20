package com.example.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.data.User
import com.example.socialmediaapp.databinding.ActivityMainFragmentProfileBinding
import com.example.socialmediaapp.databinding.ActivityMainFragmentProfileBottomSheetBinding
import com.example.utils.Const
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URI
import java.util.UUID
import kotlin.concurrent.thread

class FragmentProfile : Fragment() {

    private val binding by lazy {
        ActivityMainFragmentProfileBinding.inflate(layoutInflater)
    }


    private val bottomSheetBinding by lazy {
        ActivityMainFragmentProfileBottomSheetBinding.inflate(layoutInflater)
    }

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
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun setBottomSheetProfilePic(chosenProfilePic: Uri) {
        bottomSheetBinding.ivEditProfilePic.setImageURI(chosenProfilePic)
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

    private fun showEditBottomSheet(binding: ActivityMainFragmentProfileBinding) {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bottomSheetBinding.root)

        dialog.show()

        bottomSheetBinding.etEditName.setText(binding.tvName.text)
        bottomSheetBinding.etEditAbout.setText(binding.tvAbout.text)


        bottomSheetBinding.ivEditProfilePic.setOnClickListener {
            // launching intent for picking image from gallery
            profileImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        bottomSheetBinding.btnDone.setOnClickListener {
            // TODO: edit name, profile-pic and about and update this on fire-store.
            currentUser.fullName = bottomSheetBinding.etEditName.text.toString()
            currentUser.about = bottomSheetBinding.etEditAbout.text.toString()
            userReference.set(currentUser)
            chosenProfilePic?.let {
                updateProfilePic(it)
            }
            setFragmentDetails()


            dialog.dismiss()
        }
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
}