package com.example.candiddly

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_assign_friends.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AssignFriendActivity : AppCompatActivity() {
    private val TAG = "AssignFriendActivity"

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val docRefUsers = db.collection("users")
    private lateinit var member: User

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_friends)

        assignFriendHostButton.setOnClickListener{
            assignFriendHostButton.isEnabled = false
            assignFriendHostButton.isClickable = false
            assignFriendMemberButton.isEnabled = true
            assignFriendMemberButton.isClickable = true
            assignFriendUsernameEditText.visibility = View.VISIBLE
            assignFriendAddButton.visibility = View.VISIBLE
            assignFriendTextView.visibility = View.GONE
            assignFriendStartButton.visibility = View.GONE
        }

        assignFriendMemberButton.setOnClickListener{
            assignFriendHostButton.isEnabled = true
            assignFriendHostButton.isClickable = true
            assignFriendMemberButton.isEnabled = false
            assignFriendMemberButton.isClickable = false
            assignFriendUsernameEditText.visibility = View.GONE
            assignFriendAddButton.visibility = View.GONE
            assignFriendTextView.visibility = View.VISIBLE
            assignFriendStartButton.visibility = View.VISIBLE
        }

        assignFriendAddButton.setOnClickListener{
            val username = assignFriendUsernameEditText.text.toString()
            assignFriendUsernameEditText.text.clear()

            lifecycleScope.launch {
                val memberDoc = docRefUsers
                    .whereEqualTo("username", username)
                    .get()
                    .await()
                if (memberDoc.isEmpty) {
                    toastMaker("No user $username exists, please try again")
                    return@launch
                } else {
                    for (doc in memberDoc) {
                        member = doc.toObject(User::class.java)
                        db.collection("users")
                                .document(currentUser?.uid.toString())
                                .collection("events")
                                .document("group")
                                .update("group", FieldValue.arrayUnion(member.id))
                        }
                }
            }
        }

        assignFriendStartButton.setOnClickListener {
            var groupList: MutableList<String>
            lifecycleScope.launch {
                val document = db.collection("users")
                    .document(currentUser?.uid.toString())
                    .collection("events")
                    .document("group")
                    .get()
                    .await()

                if (document.data!!.isEmpty()) {
                    return@launch
                }

                groupList = document.toObject(IDList::class.java)?.group!!
                if (!assignFriendHostButton.isEnabled) {
                    if (currentUser?.uid.toString() !in groupList) {
                        groupList.add(currentUser?.uid.toString())
                    }
                    val nestedData = hashMapOf(
                        "group" to groupList
                    )
                    Log.d(TAG, "grouplist = $groupList")

                    for (user in groupList) {
                        Log.d(TAG, "user id = $user")
                        db.collection("users")
                            .document(user)
                            .collection("events")
                            .document("group")
                            .set(nestedData)
                    }
                }

                if (groupList.indexOf(currentUser?.uid.toString()) == groupList.size -1) {
                    val friendDoc = docRefUsers.document(groupList[0]).get().await()
                    val friend = friendDoc.toObject(User::class.java)
                    if (friend != null) {
                        assignFriendTextView.text = "You will be taking candids of ${friend.username}"
                    }
                } else {
                    val index = groupList.indexOf(currentUser?.uid.toString()) + 1
                    val friendDoc = docRefUsers.document(groupList[index]).get().await()
                    val friend = friendDoc.toObject(User::class.java)
                    if (friend != null) {
                        assignFriendTextView.text = "You will be taking candids of ${friend.username}"
                    }
                }
            }
        }

        assignFriendHostButton.performClick()
    }

    override fun onDestroy() {
        super.onDestroy()
        val docRef = db.collection("users")
            .document(currentUser?.uid.toString())
            .collection("events")
            .document("group")

        val updates = hashMapOf<String, Any>(
            "group" to FieldValue.delete()
        )
        docRef.update(updates)
    }


    private fun toastMaker(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
