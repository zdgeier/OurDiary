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

        fun updatePhoto(documentReference: DocumentReference, storageReference: StorageReference, jpegFile: Uri) {
            // Create the file metadata
            val metadata = StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build()

            val name = jpegFile.lastPathSegment

            // Upload file and metadata to the path 'images/mountains.jpg'
            val uploadTask = storageReference.putFile(jpegFile, metadata)

            // Listen for state changes, errors, and completion of the upload.
            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                System.out.println("Upload is $progress% done")
            }.addOnPausedListener {
                System.out.println("Upload is paused")
            }.addOnFailureListener {
                System.out.println("Upload failed")
            }.addOnSuccessListener {
                documentReference.update("image", name)
            }
        }

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