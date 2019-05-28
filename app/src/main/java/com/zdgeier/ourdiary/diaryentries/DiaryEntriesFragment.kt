package com.zdgeier.ourdiary.diaryentries


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.zdgeier.ourdiary.R
import kotlinx.android.synthetic.main.fragment_diary_entries.view.*

class DiaryEntriesFragment : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: DiaryEntryFirestoreRecyclerAdapter? = null

    private val rootRef = FirebaseFirestore.getInstance()
    private val settings = FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build()

    private val query = rootRef.collection("diaryentries").orderBy("time", Query.Direction.DESCENDING)
    private val options = FirestoreRecyclerOptions.Builder<DiaryEntry>()
        .setQuery(query, DiaryEntry::class.java)
        .setLifecycleOwner(this)
        .build()

    init {
        rootRef.firestoreSettings = settings
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_diary_entries, container, false)

        // Recycler View:
        layoutManager = LinearLayoutManager(v.context)
        v.diaryEntriesRecyclerView.layoutManager = layoutManager

        adapter = DiaryEntryFirestoreRecyclerAdapter(options)
        v.diaryEntriesRecyclerView.adapter = adapter

        return v
    }


}
