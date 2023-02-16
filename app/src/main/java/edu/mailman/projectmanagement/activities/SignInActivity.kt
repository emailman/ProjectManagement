package edu.mailman.projectmanagement.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowInsets
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import edu.mailman.projectmanagement.R
import edu.mailman.projectmanagement.databinding.ActivitySigninBinding
import edu.mailman.projectmanagement.models.User

class SignInActivity : BaseActivity() {
    private var binding: ActivitySigninBinding? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        //This call the parent constructor
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.insetsController?.hide(WindowInsets.Type.statusBars())

        auth = FirebaseAuth.getInstance()

        binding?.btnSignIn?.setOnClickListener {
            signInRegisteredUser()
        }

        setupActionBar()
    }

    fun signInSuccess(user: User) {
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
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

    private fun signInRegisteredUser() {
        val email = binding?.etEmail?.text?.toString()?.trim {it <= ' '}
        val password = binding?.etPassword?.text?.toString()?.trim {it <= ' '}

        if (validateForm(email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))

            auth.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("sign in", "createUserWithEmail:success")
                        val user = auth.currentUser
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("sign in", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateForm(email: String?, password: String?) : Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email address")
                false
            }

            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password")
                false
            }
            else -> { true }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
