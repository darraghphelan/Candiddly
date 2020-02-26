package com.example.candiddly

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    private lateinit var logoutButton: Button
    private lateinit var friendButton: Button
    private lateinit var updatePasswordButton: Button
    private lateinit var usernameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val docRef = db.collection("users").document(user?.uid.toString())


        friendButton = findViewById(R.id.mainFriendsButton)
        logoutButton = findViewById(R.id.mainLogoutButton)
        updatePasswordButton = findViewById(R.id.mainUpdatePasswordButton)

//        db.collection("users")
//            .whereEqualTo("username", "Diddily")
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    usernameTextView.append(document.data?.get("username").toString())
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents: ", exception)
//            }
//
//        docRef.get()
//            .addOnSuccessListener { document ->
//                if (document != null) {
//                    usernameTextView.append(document.data?.get("username").toString())
//                    Log.d(TAG, "DocumentSnapshot data: ${document.data?.get("username").toString()}")
//                } else {
//                    Log.d(TAG, "No such document")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "get failed with ", exception)
//            }
//
//        val test = IDList(listOf("dwDJukLt3ygOBdiB1gINpYlp6Hy1", "jeKAzAngPEMWtfJxOoQQmpDnF1A3"))
//        db.collection("users").document(user?.uid.toString()).collection("requests").document("friends").update("friends", FieldValue.arrayUnion("TOCrhNM9wGXS96jTp7iplh5y0bw1"))
//
//        val docRef2 = db.collection("users").document(user?.uid.toString()).collection("requests").document("friends")
//        docRef2.get()
//            .addOnSuccessListener { document ->
//                val list = document.toObject(IDList::class.java)?.friends
//                if (document != null) {
//                    for (item in list!!){
//                        Log.d(TAG, item)
//                    }
//                    Log.d(TAG, document.data.toString())
//                } else {
//                    Log.d(TAG, "No such document")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "get failed with ", exception)
//            }

        if (auth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Already logged in", Toast.LENGTH_LONG).show()
        }


        friendButton.setOnClickListener {
            val intent = Intent(this, ConnectionActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        updatePasswordButton.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }


    }

}
