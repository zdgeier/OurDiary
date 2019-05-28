package com.zdgeier.ourdiary

import android.location.Location
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MyFirebaseStorage {
    companion object {
        fun uploadCurrentDiaryEntry(documentReference: DocumentReference, diaryEntry: DiaryEntry) {
            val upload = HashMap<String, Any>()
            upload["time"] = diaryEntry.time
            upload["date"] = SimpleDateFormat("MM-dd-yyyy", Locale.US).format(diaryEntry.time.toDate())
            upload["description"] = diaryEntry.text

            documentReference.set(diaryEntry)
                .addOnSuccessListener {
                    Log.d("New Entry", "New ENTRY DocumentSnapshot added")
                }
                .addOnFailureListener { e ->
                    Log.w("New Entry", "New ENTRY Error adding document", e)
                }
        }
    }
}