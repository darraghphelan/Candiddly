package com.example.candiddly

import classes.User
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sender.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SenderActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private val docRefUsers = db.collection("users")
    private lateinit var receiver: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sender)

        senderTakePictureButton.setOnClickListener {
            val username = senderUsernameEditText.text.toString()

            lifecycleScope.launch {
                val receiverDoc = docRefUsers
                    .whereEqualTo("username", username)
                    .get()
                    .await()
                if (receiverDoc.isEmpty) {
                    displayError("No user $username exists, please try again")
                    senderUsernameEditText.text.clear()
                    return@launch
                } else {
                    for (doc in receiverDoc) {
                        receiver = doc.toObject(User::class.java)
                        intentMaker(receiver.id)
                    }
                }
            }
        }
    }

    private fun intentMaker(receiverID: String) {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("ReceiverID", receiverID)
        startActivity(intent)
    }

    private fun displayError(message: String) {
        errorTextView.text = message
    }
}
