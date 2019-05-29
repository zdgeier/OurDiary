package com.zdgeier.ourdiary.editentry


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.zdgeier.ourdiary.R
import com.zdgeier.ourdiary.entrylist.DiaryEntry
import kotlinx.android.synthetic.main.fragment_edit_entry.view.*


class EditEntryFragment : Fragment() {
    companion object {
        const val ARG_ENTRY_DOCUMENT_PATH = "entryDocumentPath"
    }


    private lateinit var entryReference: DocumentReference

    private val rootRef = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_edit_entry, container, false)

        val documentPath = arguments?.getString(ARG_ENTRY_DOCUMENT_PATH)

        documentPath?.let {
            entryReference = rootRef.document(documentPath)

            entryReference.get().addOnSuccessListener { documentSnapshot ->
                val entry = documentSnapshot.toObject(DiaryEntry::class.java)
                v.editEntryText.setText(entry?.text)
            }
        }

        v.confirmEditEntryButton.setOnClickListener {
            entryReference.update("text", v.editEntryText.text.toString())
            findNavController().popBackStack()
        }

        return v
    }


}
