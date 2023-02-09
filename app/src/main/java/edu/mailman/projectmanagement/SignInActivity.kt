package edu.mailman.projectmanagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import edu.mailman.projectmanagement.databinding.ActivitySigninBinding

class SignInActivity : AppCompatActivity() {
    private var binding: ActivitySigninBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        //This call the parent constructor
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.insetsController?.hide(WindowInsets.Type.statusBars())

        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarSignInActivity)


        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            binding?.toolbarSignInActivity?.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}
