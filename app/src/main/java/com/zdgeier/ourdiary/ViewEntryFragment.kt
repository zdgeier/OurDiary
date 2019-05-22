package com.zdgeier.ourdiary


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_view_entry.view.*

class ViewEntryFragment : Fragment() {

    private val storageRef = FirebaseStorage.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_view_entry, container, false)

        arguments?.getString("image")?.let {
            GlideApp.with(context!! /* context */)
                .load(storageRef.child(it))
                .into(v.viewImageView)
        }

        return v
    }


}
