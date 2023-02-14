package edu.mailman.projectmanagement.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
            }
    }

    fun getCurrentUserId() : String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}