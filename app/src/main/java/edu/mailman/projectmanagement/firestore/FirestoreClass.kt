package edu.mailman.projectmanagement.firestore

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import edu.mailman.projectmanagement.activities.*
import edu.mailman.projectmanagement.models.Board
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
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    fun createBoard(activity: CreateBoardActivity, board: Board) {
        mFirestore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.i("Eric", "Board created successfully")
                Toast.makeText(activity, "Board created successfully",
                    Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            } .addOnFailureListener {
                exception ->
                activity.hideProgressDialog()
                Log.e("Eric", "Error creating board", exception)
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity,
                              userHashMap: HashMap<String, Any>) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i("Eric", "Profile data updated successfully")
                Toast.makeText(activity, "Profile Updated Successfully",
                    Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener {
                e ->
                activity.hideProgressDialog()
                Log.e("Eric", "Error creating a board", e)
                Toast.makeText(activity, "Profile Update Error",
                    Toast.LENGTH_SHORT).show()

            }

    }


    fun loadUserData(activity: Activity) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)

                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser!!)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser!!)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser!!)
                    }
                }
            }.addOnFailureListener {
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error reading document")
            }
    }
}