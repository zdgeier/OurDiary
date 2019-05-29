package com.zdgeier.ourdiary.createentry


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.zdgeier.ourdiary.DiaryViewModel
import com.zdgeier.ourdiary.R
import com.zdgeier.ourdiary.entrylist.DiaryEntry
import kotlinx.android.synthetic.main.fragment_create_entry.view.*

class CreateEntry : Fragment() {

    private lateinit var diaryViewModel: DiaryViewModel

    companion object {
        private val TAG = "CreateEntry"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_create_entry, container, false)

        diaryViewModel = activity?.run {
            ViewModelProviders.of(this).get(DiaryViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        v.createEntryButton.setOnClickListener {
            diaryViewModel.diaryId.value?.collection("entries")?.add(
                DiaryEntry(
                    Timestamp.now(),
                    v.createEntryEditText.text.toString()
                )
            )?.addOnSuccessListener {
                Log.d(TAG, "Created diary entry with ID: ${it.id}")
                findNavController().popBackStack()
            }?.addOnFailureListener {
                Log.d(TAG, "Failed to create diary entry ${it.printStackTrace()}")
            }
        }

        return v
    }
}
