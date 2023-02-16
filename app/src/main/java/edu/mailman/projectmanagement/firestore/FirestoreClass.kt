package edu.mailman.projectmanagement.firestore

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import edu.mailman.projectmanagement.activities.SignInActivity
import edu.mailman.projectmanagement.activities.SignupActivity
import edu.mailman.projectmanagement.models.User
import edu.mailman.projectmanagement.utils.Constants

class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignupActivity, userInfo: User) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                Log.e(activity.javaClass.simpleName, "Error writing document")
            }
    }

    fun getCurrentUserId() : String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    fun signInUser(activity: SignInActivity) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser != null) {
                    activity.signInSuccess(loggedInUser)
                }
            }.addOnFailureListener {
                Log.e(activity.javaClass.simpleName, "Error reading document")
            }
    }
}