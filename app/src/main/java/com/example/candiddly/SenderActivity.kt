package com.example.candiddly

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_assign_friends.*
import kotlinx.android.synthetic.main.activity_sender.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SenderActivity : AppCompatActivity() {

    private val TAG = "SenderActivity"

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val docRefUsers = db.collection("users")
    private lateinit var receiver: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sender)

        senderTakePictureButton.setOnClickListener {

            val username = senderUsernameEditText.text.toString()
            senderUsernameEditText.text.clear()

            lifecycleScope.launch {
                val receiverDoc = docRefUsers
                    .whereEqualTo("username", username)
                    .get()
                    .await()
                if (receiverDoc.isEmpty) {
                    toastMaker("No user $username exists, please try again")
                    return@launch
                } else {
                    for (doc in receiverDoc) {
                        receiver = doc.toObject(User::class.java)
                        Log.d(TAG, "Receiver = $receiver")
                        intentMaker(receiver.id)
                    }
                }
            }
        }
    }

    private fun intentMaker(receiverID: String) {
        Log.d(TAG, "ReceiverID = $receiverID")
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("ReceiverID", receiverID)
        startActivity(intent)

    }

    private fun toastMaker(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
