package edu.mailman.projectmanagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        window.insetsController?.hide(WindowInsets.Type.statusBars())

    }
}