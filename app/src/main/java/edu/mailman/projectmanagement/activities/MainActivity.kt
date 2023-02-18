package edu.mailman.projectmanagement.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import edu.mailman.projectmanagement.R
import edu.mailman.projectmanagement.databinding.ActivityMainBinding
import edu.mailman.projectmanagement.firestore.FirestoreClass
import edu.mailman.projectmanagement.models.User

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        binding?.navView?.setNavigationItemSelectedListener(this)

        FirestoreClass().signInUser(this)
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

    companion object {
        private const val PROFILE_ITEM_ID: Int = 0
        private const val SIGN_OUT_ITEM_ID: Int = 1
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    fun updateNavigationUserDetails(user: User) {
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
                Toast.makeText(this@MainActivity,
                    "My Profile", Toast.LENGTH_SHORT).show()
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