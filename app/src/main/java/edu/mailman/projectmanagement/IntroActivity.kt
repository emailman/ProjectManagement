package edu.mailman.projectmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import edu.mailman.projectmanagement.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private var binding: ActivityIntroBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.insetsController?.hide(WindowInsets.Type.statusBars())

        binding?.btnSignUpIntro?.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}