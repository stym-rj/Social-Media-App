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
import com.example.data.Post
import com.example.data.User
import com.example.socialmediaapp.databinding.ActivityMainFragmentPostBinding
import com.example.utils.Const
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class FragmentPost : Fragment() {

    private val binding by lazy {
        ActivityMainFragmentPostBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var mFireStore: FirebaseFirestore
    private val mFirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private lateinit var userReference: DocumentReference
    private lateinit var imageReference: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = binding.root

        // references from firebase
        mFireStore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        imageReference = mFirebaseStorage.reference.child(Const.STORAGE_POSTS)
        userReference = mFireStore.collection(Const.FS_USERS).document(auth.currentUser?.uid ?: "")


        lateinit var currentUser: User
        userReference.get()
            .addOnSuccessListener { data->
                data.toObject(User::class.java)?.let {
                    currentUser = it
                }
            }
            .addOnFailureListener {

            }

        var chosenPhoto: Uri? = null
        val profileImagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            chosenPhoto = uri
            chosenPhoto?.let{
                binding.ivPhoto.setImageURI(chosenPhoto)
                binding.tvError.visibility = View.GONE
            }
        }
        binding.btnSelectImage.setOnClickListener {
            // launching intent for picking image from gallery
            profileImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }

        binding.btnPost.setOnClickListener {
            if (chosenPhoto != null) {
                // upload to Firebase
                val ref = imageReference.child(UUID.randomUUID().toString())
                ref.putFile(chosenPhoto!!)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            currentUser.posts.add(
                                Post(
                                    imageUrl = it.toString(),
                                    caption = binding.etCaption.text.toString()
                                )
                            )
                            userReference.set(currentUser)
                            Toast.makeText(requireContext(), "Posted Successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {

                    }

            } else {
                binding.tvError.visibility = View.VISIBLE
            }
        }



        return view

    }
}