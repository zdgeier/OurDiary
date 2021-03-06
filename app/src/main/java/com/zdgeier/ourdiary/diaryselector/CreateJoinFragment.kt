package com.zdgeier.ourdiary.diaryselector


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.zdgeier.ourdiary.DiaryViewModel

import com.zdgeier.ourdiary.R
import kotlinx.android.synthetic.main.fragment_create_join.view.*

class CreateJoinFragment : Fragment() {
    private val settings = FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
        .build()
    private val rootRef = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var model: DiaryViewModel

    companion object {
        private val TAG = "CreateJoinFragment"
    }

    init {
        rootRef.firestoreSettings = settings
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_create_join, container, false)

        model = activity?.run {
            ViewModelProviders.of(this).get(DiaryViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        v.createButton.setOnClickListener {
            val docData = HashMap<String, Any?>()
            docData["users"] = arrayListOf(auth.currentUser?.uid)
            docData["created"] = Timestamp.now()

            rootRef.collection("diaries").add(docData)
                .addOnSuccessListener {
                    Log.d(TAG, "Created dairy with ID: ${it.id}")
                    model.diaryId.value = it

                    findNavController().navigate(R.id.action_createJoinFragment_to_diaryFragment)
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to create diary")
                }
        }

        v.joinButton.setOnClickListener {
            val diaryName = v.joinCodeEditText.text.toString()
            model.diaryId.value = rootRef.collection("diaries").document(diaryName)

            findNavController().navigate(R.id.action_createJoinFragment_to_diaryFragment)
        }

        return v
    }


}
