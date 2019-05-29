package com.zdgeier.ourdiary.entrylist


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.zdgeier.ourdiary.DiaryViewModel
import com.zdgeier.ourdiary.R
import kotlinx.android.synthetic.main.fragment_entry_list.view.*

class EntryListFragment : Fragment(), DiaryEntriesRecyclerAdapter.OnPopupItemSelectedListener {
    companion object {
        private val TAG = "EntryListFragment"
    }

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: DiaryEntriesRecyclerAdapter? = null

    private lateinit var diaryViewModel: DiaryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_entry_list, container, false)

        diaryViewModel = activity?.run {
            ViewModelProviders.of(this).get(DiaryViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        diaryViewModel.diaryId.observe(this, Observer {
            val options = getOptions(getQuery(it))

            // Recycler View:
            layoutManager = LinearLayoutManager(v.context)
            v.diaryEntriesRecyclerView.layoutManager = layoutManager

            adapter = DiaryEntriesRecyclerAdapter(options, this)
            v.diaryEntriesRecyclerView.adapter = adapter
        })

        return v
    }

    override fun onPopupItemSelected(itemId: Int, entryReference: DocumentReference) {
        when (itemId) {
            R.id.editDropdown -> Log.d(TAG, "Edit pressed")
            R.id.deleteDropdown -> entryReference.delete()
        }
    }

    private fun getQuery(diaryReference : DocumentReference) =
        diaryReference.collection("entries")
            .orderBy("time", Query.Direction.DESCENDING)

    private fun getOptions(query: Query) =
        FirestoreRecyclerOptions.Builder<DiaryEntry>()
        .setQuery(query, DiaryEntry::class.java)
        .setLifecycleOwner(this)
        .build()
}
