package edu.mailman.projectmanagement.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import edu.mailman.projectmanagement.R
import edu.mailman.projectmanagement.databinding.ActivitySignupBinding
import edu.mailman.projectmanagement.firestore.FirestoreClass
import edu.mailman.projectmanagement.models.User

class SignupActivity : BaseActivity() {
    private var binding: ActivitySignupBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.insetsController?.hide(WindowInsets.Type.statusBars())

        setupActionBar()
    }

    fun userRegisteredSuccess() {
        Toast.makeText(
            this,
            "You have successfully registered",
            Toast.LENGTH_LONG
        ).show()
        FirebaseAuth.getInstance().signOut()
        finish()
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

            binding?.btnSignUp?.setOnClickListener {
                registerUser()
            }
        }
    }

    private fun registerUser() {
        val name = binding?.etName?.text?.toString()?.trim {it <= ' '}
        val email = binding?.etEmail?.text?.toString()?.trim {it <= ' '}
        val password = binding?.etPassword?.text?.toString()?.trim {it <= ' '}

        if (validateForm(name, email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().
            createUserWithEmailAndPassword(email!!, password!!).addOnCompleteListener { task ->
                hideProgressDialog()
                if (task.isSuccessful) {
                    val firebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email
                    val user = User(firebaseUser.uid, name!!, registeredEmail!!)
                    FirestoreClass().registerUser(this, user)
                } else {
                    Toast.makeText(
                        this,
                        "$name registration failed", Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun validateForm(name: String?, email: String?, password: String?) : Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter a name")
                false
            }

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