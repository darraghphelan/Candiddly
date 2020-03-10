package com.example.candiddly

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterActivity : AppCompatActivity() {

    private val TAG = "RegisterActivity"

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    private lateinit var registerButton: Button
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        usernameEditText = findViewById(R.id.registerUsernameEditText)
        emailEditText = findViewById(R.id.registerEmailEditText)
        passwordEditText = findViewById(R.id.registerPasswordEditText)

        loginButton = findViewById(R.id.registerLoginButton)
        registerButton = findViewById(R.id.registerRegisterButton)


        loginButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        registerButton.setOnClickListener{
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                toastMaker("Please fill all the fields")
            } else {
                lifecycleScope.launch {
                    val docRefUsers = db.collection("users")
                    val userDocs = docRefUsers
                        .whereEqualTo("username", username)
                        .get()
                        .await()
                    if (userDocs.isEmpty) {
                        registration(email, password, username)
                    } else {
                        toastMaker("Username taken")
                    }
                }
            }
        }
    }


    private fun registration(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this)
        { task ->
            if (task.isSuccessful) {

                val users = db.collection("users")
                val currentUser = FirebaseAuth.getInstance().currentUser

                val user = hashMapOf(
                    "email" to email,
                    "username" to username,
                    "id" to currentUser?.uid.toString()
                )

                users.document(currentUser?.uid.toString()).set(user)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: $documentReference")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }

                val connections =
                    users.document(currentUser?.uid.toString()).collection("connections")
                val gallery = users.document(currentUser?.uid.toString()).collection("gallery")

                val friendsData = hashMapOf("friends" to arrayListOf(""))
                connections.document("friends").set(friendsData)

                val receivedData = hashMapOf("received" to arrayListOf(""))
                connections.document("received").set(receivedData)

                val sentData = hashMapOf("sent" to arrayListOf(""))
                connections.document("sent").set(sentData)

                val imageUrls = hashMapOf("images" to arrayListOf(""))
                gallery.document("images").set(imageUrls)
                db.collection("users").document(currentUser?.uid.toString()).collection("gallery").document("images").update("images", FieldValue.arrayRemove(""))

                toastMaker("Successfully Registered")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                toastMaker("Registration Failed")
            }
        }
    }

    private fun toastMaker(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
