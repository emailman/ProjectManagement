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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import edu.mailman.projectmanagement.R
import edu.mailman.projectmanagement.databinding.ActivityMyProfileBinding
import edu.mailman.projectmanagement.firestore.FirestoreClass
import edu.mailman.projectmanagement.models.User
import edu.mailman.projectmanagement.utils.Constants
import java.io.IOException

class MyProfileActivity : BaseActivity() {
    private var binding: ActivityMyProfileBinding? = null

    private val startUpdateActivityAndGetResult =
        registerForActivityResult(
            ActivityResultContracts
            .StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK &&
                // result.requestCode == PICK_IMAGE_REQUEST_CODE &&
                result.data!!.data != null) {
                selectedImageFileUri = result.data!!.data
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


    private var selectedImageFileUri: Uri? = null
    private var profileImageURL = ""
    private lateinit var mUserDetails: User

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
                Constants.READ_STORAGE_PERMISSION_CODE )
                Log.i("eric", "permission was requested")
            }
        }

        binding?.btnUpdate?.setOnClickListener {
            if (selectedImageFileUri != null) {
                uploadUserImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
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
        startUpdateActivityAndGetResult.launch(galleryIntent)
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
        mUserDetails = user
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

    private fun updateUserProfileData() {
        val userHashMap = HashMap<String, Any>()

        if (profileImageURL.isNotEmpty() &&
                profileImageURL != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = profileImageURL
        }

        if (binding?.etName?.text.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = (binding?.etName?.text.toString())
        }

        if (binding?.etMobile?.text.toString() != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = (binding?.etMobile?.text.toString().toLong())
        }

        FirestoreClass().updateUserProfileData(this, userHashMap)
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
                    updateUserProfileData()
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

    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}