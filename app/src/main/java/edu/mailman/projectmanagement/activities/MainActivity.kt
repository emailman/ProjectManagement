package edu.mailman.projectmanagement.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import edu.mailman.projectmanagement.R
import edu.mailman.projectmanagement.databinding.ActivityMainBinding
import edu.mailman.projectmanagement.firestore.FirestoreClass
import edu.mailman.projectmanagement.models.User
import edu.mailman.projectmanagement.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var binding: ActivityMainBinding? = null

    private val startUpdateActivityAndGetResult =
        registerForActivityResult(ActivityResultContracts
            .StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                FirestoreClass().loadUserData(this)
            } else {
                Log.e("eric", "Profile update cancelled by user")
            }
        }

    private lateinit var mUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        binding?.navView?.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this)

        val fabCreateBoard = findViewById<FloatingActionButton>(R.id.fab_create_board)

        // Note user name is set by calling Firestore above
        fabCreateBoard.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivity(intent)
        }
    }

    private fun setupActionBar() {
        val toolbarMainActivity =
            findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main_activity)

        setSupportActionBar(toolbarMainActivity)
        toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbarMainActivity.setNavigationOnClickListener{
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        if (binding?.drawLayout?.isDrawerOpen(GravityCompat.START) == true) {
            binding?.drawLayout?.closeDrawer(GravityCompat.START)
        } else {
            binding?.drawLayout?.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding?.drawLayout?.isDrawerOpen(GravityCompat.START) == true) {
            binding?.drawLayout?.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    fun updateNavigationUserDetails(user: User) {
        mUserName = user.name

        val navUserImage = findViewById<ImageView>(R.id.nav_user_image)

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)

        val userName = findViewById<TextView>(R.id.tv_username)
        userName.text = user.name
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                startUpdateActivityAndGetResult.launch(Intent(this,
                    MyProfileActivity::class.java))
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding?.drawLayout?.closeDrawer(GravityCompat.START)
        return true
    }
}