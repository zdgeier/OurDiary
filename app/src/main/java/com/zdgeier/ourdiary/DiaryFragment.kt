package com.zdgeier.ourdiary


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_diary.view.*

class DiaryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_diary, container, false)

        v.createButton.setOnClickListener {
            findNavController().navigate(R.id.action_diaryFragment_to_createEntry)
        }

        return v
    }


}
