package com.zdgeier.ourdiary

import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import org.w3c.dom.Document
import java.text.SimpleDateFormat
import java.util.*

class NewEntryViewModel : ViewModel() {

    private val rootRef = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance()

    private val date = MutableLiveData<Date>()
    val dateString = Transformations.map(date) {
        date -> SimpleDateFormat.getDateInstance().format(date)
    }

    private val text = MutableLiveData<String>()
    private val bitmap = MutableLiveData<Bitmap>()
    val photoPath = MutableLiveData<String>()
    val document = MutableLiveData<String>()

    fun setMainText(s: String?) {
        text.value = s
    }

    fun getMainText() : LiveData<String> {
        return text
    }

    fun getBitmap() : LiveData<Bitmap> {
        return bitmap
    }

    fun setBitmap(b: Bitmap?) {
        bitmap.value = b
    }

    fun setDate(d : Date?) {
        date.value = d
    }

    fun publishEntry(photoUri: Uri?, location: Location?) {
        val diaryEntry = DiaryEntry(
            time = Timestamp.now(),
            text = text.value.orEmpty()
        )

        val documentReference = rootRef.collection("diary_entries").document()

        MyFirebaseStorage.uploadCurrentDiaryEntry(documentReference, diaryEntry)

        documentReference.set(diaryEntry)
            .addOnSuccessListener {
                Log.d("New Entry", "New ENTRY DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Log.w("New Entry", "New ENTRY Error adding document", e)
            }

        photoUri?.let { uri ->
            // Create the file metadata
            val metadata = StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build()

            val name = uri.lastPathSegment

            // Upload file and metadata to the path 'images/mountains.jpg'
            val uploadTask = storageRef.reference.child("images/$name").putFile(uri, metadata)

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


        try {
            location?.let {
                val strLongitude = Location.convert(location.longitude, 0)
                val strLatitude = Location.convert(location.latitude, 0)
                documentReference.update("location", "Lat: $strLatitude   Long: $strLongitude")
            }
        }
        catch( e : SecurityException ){
            e.printStackTrace()
        }
    }
}
