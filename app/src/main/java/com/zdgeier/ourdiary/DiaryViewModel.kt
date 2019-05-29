package com.zdgeier.ourdiary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference

class DiaryViewModel : ViewModel() {
    val diaryId = MutableLiveData<DocumentReference>()
}