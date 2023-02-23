package edu.mailman.projectmanagement.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import edu.mailman.projectmanagement.R
import edu.mailman.projectmanagement.databinding.ActivityMyProfileBinding
import edu.mailman.projectmanagement.firestore.FirestoreClass
import edu.mailman.projectmanagement.models.User
import java.io.IOException

class MyProfileActivity : BaseActivity() {
    private var binding: ActivityMyProfileBinding? = null

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    private var selectedImageFileUri: Uri? = null
    private var profileImageURL = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        FirestoreClass().loadUserData(this)

        binding?.ivUserImage?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
                Log.i("eric", "permission was granted")

            } else {
                ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_CODE )
                Log.i("eric", "permission was requested")
            }
        }

        binding?.btnUpdate?.setOnClickListener {
            if (selectedImageFileUri != null) {
                uploadUserImage()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()

            } else {
                Toast.makeText(this,"Permission not allowed," +
                        " but you can allow it from Settings", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showImageChooser() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("eric", "handling activity result")

        if (resultCode == Activity.RESULT_OK &&
                requestCode == PICK_IMAGE_REQUEST_CODE &&
                data!!.data != null) {
            selectedImageFileUri = data.data
            Log.i("eric", "got an image")

            try {
                Glide
                    .with(this@MyProfileActivity)
                    .load(selectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding?.ivUserImage as ImageView)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }

        binding?.toolbarMyProfileActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun setUserDataInUI(user: User) {
        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding?.ivUserImage!!)

        binding?.etName?.setText(user.name)
        binding?.etEmail?.setText(user.email)
        if (user.mobile != 0L) {
            binding?.etMobile?.setText(user.mobile.toString())
        }
    }

    private fun uploadUserImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        if (selectedImageFileUri != null) {
            val sRef = FirebaseStorage
                .getInstance()
                .reference.child("USER_IMAGE"
                        + System.currentTimeMillis() + "."
                        + getFileExtension(selectedImageFileUri))
            sRef.putFile(selectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                Log.i("Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i("Downloadable Image URL", uri.toString())
                    Log.i("eric", "download image url succeeded")
                    profileImageURL = uri.toString()
                    hideProgressDialog()
                    // TODO update user profile data
                }
            }.removeOnFailureListener {
                exception ->
                Toast.makeText(this@MyProfileActivity,
                exception.message,
                Toast.LENGTH_LONG).show()
                hideProgressDialog()
                Log.i("eric", "download image url failed")
            }
        }
    }

    private fun getFileExtension(uri: Uri?) : String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
}