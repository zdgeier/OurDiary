package com.zdgeier.ourdiary

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_new_entry.view.*


class NewEntryFragment : Fragment() {

    private lateinit var model: NewEntryViewModel
    private var photoUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val storageRef = FirebaseStorage.getInstance()

    private val REQUEST_TAKE_PHOTO = 1

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat.getDateTimeInstance().format(Date())
        val storageDir: File? = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    photoUri = FileProvider.getUriForFile(
                        context!!,
                        getString(R.string.file_provider),
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    ///////////taken from https://developer.android.com/training/camera/photobasics/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
                photoUri?.let {
                    val cr = context?.contentResolver
                    cr?.notifyChange(it, null)
                    try {
                        model.setBitmap(MediaStore.Images.Media.getBitmap(cr, it))
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        catch (e : Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_new_entry, container, false)

        model = activity?.run {
            ViewModelProviders.of(this).get(NewEntryViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        model.dateString.observe(this, Observer {
            v.newEntryDateTextView.text = it
        })

        model.getMainText().observe(this, Observer {
            if (it != v.newEntryTextEditText.text.toString()) {
                v.newEntryTextEditText.setText(it)
            }
        })

        model.getBitmap().observe(this, Observer {
            v.newEntryImage.setImageBitmap(it)
        })

        model.photoPath.observe(this, Observer {
            if (it == null) {
                v.newEntryImage.setImageDrawable(context?.getDrawable(R.drawable.ic_photo_camera_black_24dp))
            }
            else {
                GlideApp.with(context!! /* context */)
                    .load(storageRef.reference.child("images/$it"))
                    .placeholder(R.drawable.ic_photo_camera_black_24dp)
                    .into(v.newEntryImage)
            }
        })

        v.newEntryImage.setOnClickListener {
            dispatchTakePictureIntent()
        }

        v.newEntryTextEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                model.setMainText(s.toString())
            }
        })

        v.newEntryButton.setOnClickListener {
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        model.publishEntry(photoUri, location)
                    }
                    .addOnFailureListener {
                        model.publishEntry(photoUri, null)
                    }
            }
            catch (e : SecurityException) {
                model.publishEntry(photoUri, null)
            }

            Navigation.findNavController(v).popBackStack()
        }
        return v
    }
}
