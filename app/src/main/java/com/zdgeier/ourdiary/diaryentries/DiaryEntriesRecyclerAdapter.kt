package com.zdgeier.ourdiary.diaryentries

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.zdgeier.ourdiary.R
import kotlinx.android.synthetic.main.text_list_item.view.*

class DiaryEntryFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<DiaryEntry>) :
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

    private fun showPopupMenu(view : View, diaryEntry : DiaryEntry, position: Int) {
        val popup = PopupMenu(view.context, view)
        popup.menuInflater.inflate(R.menu.card, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.editDropdown -> null // TODO add edit action
                R.id.deleteDropdown -> null // TODO add delete action
            }
            true
        }
        popup.show()
    }

    private inner class TextDiaryEntryViewHolder(v : View) : DiaryEntryViewHolder(v) {
        override fun setDiaryEntry(diaryEntry : DiaryEntry) {
            v.primary_text.text = diaryEntry.getTimeDisplay()
            v.sub_text.text = diaryEntry.getLocationDisplay()
            v.text_main_text.text = diaryEntry.getTextDisplay()

            v.textImageButton.setOnClickListener{
                showPopupMenu(v.textImageButton, diaryEntry, layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryEntryViewHolder {
        return when (viewType) {
            TEXT_TYPE -> TextDiaryEntryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.text_list_item, parent, false))
            else -> TextDiaryEntryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.text_list_item, parent, false)) // TODO replace with image
        }
    }
}

abstract class DiaryEntryViewHolder(val v : View) : RecyclerView.ViewHolder(v) {
    abstract fun setDiaryEntry(diaryEntry: DiaryEntry)
}