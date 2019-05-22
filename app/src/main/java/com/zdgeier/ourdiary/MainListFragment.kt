package com.zdgeier.ourdiary

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.image_list_item.view.*
import kotlinx.android.synthetic.main.text_list_item.view.*
import java.text.SimpleDateFormat



class MainListFragment : Fragment() {
    private val rootRef = FirebaseFirestore.getInstance()
    private val settings = FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build()
    private val query = rootRef.collection("diary_entries").orderBy("time", Query.Direction.DESCENDING)
    private val options = FirestoreRecyclerOptions.Builder<DiaryEntry>()
        .setQuery(query, DiaryEntry::class.java)
        .setLifecycleOwner(this@MainListFragment)
        .build()

    private val storageRef = FirebaseStorage.getInstance().reference

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: DiaryEntryFirestoreRecyclerAdapter? = null

    private lateinit var newEntryModel : NewEntryViewModel

    private val TAG = "MainListFragment"

    init {
        rootRef.firestoreSettings = settings
    }

    private inner class DiaryEntryFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<DiaryEntry>) :
        FirestoreRecyclerAdapter<DiaryEntry, DiaryEntryViewHolder>(options) {

        private val TEXT_TYPE = 0
        private val IMAGE_TYPE = 1

        override fun onBindViewHolder(diaryEntryViewHolder: DiaryEntryViewHolder,
                                      position: Int,
                                      diaryEntry: DiaryEntry) =
            diaryEntryViewHolder.setDiaryEntry(diaryEntry)

        override fun getItemViewType(position: Int): Int {
            return when (getItem(position).image) {
                "" -> TEXT_TYPE
                else -> IMAGE_TYPE
            }
        }

        private inner class ImageDiaryEntryViewHolder(v : View) : DiaryEntryViewHolder(v) {
            override fun setDiaryEntry(diaryEntry : DiaryEntry) {
                val sfd = SimpleDateFormat.getDateInstance()
                v.title_text.text = sfd.format(diaryEntry.time.toDate())
                v.subtitle_text.text = diaryEntry.location
                v.image_main_text.text = diaryEntry.text

                GlideApp.with(context!! /* context */)
                    .load(storageRef.child("images/${diaryEntry.image}"))
                    .into(v.media_image)

                v.media_image.setOnClickListener {
                    Navigation.findNavController(v).navigate(R.id.action_main_list3_to_viewEntryFragment,
                        bundleOf(
                            "image" to "images/${diaryEntry.image}"
                        )
                    )
                }

                v.imageButton.setOnClickListener{
                    showPopupMenu(v.imageButton, diaryEntry, layoutPosition)
                }
            }
        }

        private fun showPopupMenu(view : View, diaryEntry : DiaryEntry, position: Int) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.card, popup.menu)
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.editDropdown -> edit(diaryEntry, snapshots.getSnapshot(position).reference.id)
                    R.id.deleteDropdown -> snapshots.getSnapshot(position).reference.delete()
                }
                true
            }
            popup.show()
        }

        private inner class TextDiaryEntryViewHolder(v : View) : DiaryEntryViewHolder(v) {
            override fun setDiaryEntry(diaryEntry : DiaryEntry) {
                val sfd = SimpleDateFormat.getDateInstance()
                v.primary_text.text = sfd.format(diaryEntry.time.toDate())
                v.sub_text.text = diaryEntry.location
                v.text_main_text.text = diaryEntry.text

                v.textImageButton.setOnClickListener{
                    showPopupMenu(v.textImageButton, diaryEntry, layoutPosition)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryEntryViewHolder {
            return when (viewType) {
                TEXT_TYPE -> TextDiaryEntryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.text_list_item, parent, false))
                else -> ImageDiaryEntryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_list_item, parent, false))
            }
        }
    }

    private abstract class DiaryEntryViewHolder(val v : View) : RecyclerView.ViewHolder(v) {
        abstract fun setDiaryEntry(diaryEntry: DiaryEntry)
    }

    private fun edit(diaryEntry: DiaryEntry, id: String) {
        newEntryModel.setMainText(diaryEntry.text)
        newEntryModel.setDate(diaryEntry.time.toDate())
        newEntryModel.photoPath.value = diaryEntry.image
        newEntryModel.document.value = id
        Navigation.findNavController(view!!).navigate(R.id.action_main_list3_to_newEntryFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v =  inflater.inflate(R.layout.fragment_main_list, container, false)

        newEntryModel = activity?.run {
            ViewModelProviders.of(this).get(NewEntryViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        // Recycler View:
        layoutManager = LinearLayoutManager(v.context)
        v.findViewById<RecyclerView>(R.id.recycle).layoutManager = layoutManager

        adapter = DiaryEntryFirestoreRecyclerAdapter(options)
        v.findViewById<RecyclerView>(R.id.recycle).adapter = adapter

        //val editEntryViewModel = ViewModelProviders.of(this).get(EditEntryViewModel::class.java)
        v.findViewById<FloatingActionButton>(R.id.new_entry_button).setOnClickListener { view ->
            newEntryModel.setDate(Timestamp.now().toDate())
            newEntryModel.photoPath.value = null
            newEntryModel.document.value = null
            newEntryModel.setMainText("")
            Navigation.findNavController(view).navigate(R.id.action_main_list3_to_newEntryFragment)
        }

        return v
    }
}
