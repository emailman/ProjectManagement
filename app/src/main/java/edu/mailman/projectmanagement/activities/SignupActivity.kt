package edu.mailman.projectmanagement.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import edu.mailman.projectmanagement.R
import edu.mailman.projectmanagement.databinding.ActivitySignupBinding

class SignupActivity : BaseActivity() {
    private var binding: ActivitySignupBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.insetsController?.hide(WindowInsets.Type.statusBars())

        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarSignUpActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)

            binding?.toolbarSignUpActivity?.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}