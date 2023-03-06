package edu.mailman.projectmanagement.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
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
import edu.mailman.projectmanagement.R
import edu.mailman.projectmanagement.databinding.ActivityCreateBoardBinding
// import edu.mailman.projectmanagement.models.User
import edu.mailman.projectmanagement.utils.Constants
import java.io.IOException

class CreateBoardActivity : AppCompatActivity() {
    private var binding: ActivityCreateBoardBinding? = null

    private var selectedImageFileUri: Uri? = null
    // private var profileImageURL = ""
    // private lateinit var mUserDetails: User

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
                        .with(this@CreateBoardActivity)
                        .load(selectedImageFileUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_board_place_holder)
                        .into(binding?.ivBoardImage as ImageView)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        binding?.ivBoardImage?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                showImageChooser()
                Log.i("eric", "permission was granted")

            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
                Log.i("eric", "permission was requested")
            }
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarCreateBoardActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
            actionBar.title = resources.getString(R.string.create_board_title)
        }

        binding?.toolbarCreateBoardActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun showImageChooser() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startUpdateActivityAndGetResult.launch(galleryIntent)
    }

    private fun getFileExtension(uri: Uri?) : String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri!!))
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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}