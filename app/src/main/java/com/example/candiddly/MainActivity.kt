package com.example.candiddly

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        mainCameraButton.setOnClickListener {
            val intent = Intent(this, ConnectionActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Choose a friend to send!", Toast.LENGTH_LONG).show()
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()

            when (menuItem.itemId) {
                R.id.mainStartEventButton -> {
                    val intent = Intent(this, AssignFriendActivity::class.java)
                    startActivity(intent)
                }
                R.id.mainConnectionsButton -> {
                    val intent = Intent(this, ConnectionActivity::class.java)
                    startActivity(intent)
                }
                R.id.mainUpdatePasswordButton -> {
                    val intent = Intent(this, ResetPasswordActivity::class.java)
                    startActivity(intent)
                }
                R.id.mainViewPicturesButton -> {
                    val intent = Intent(this, ViewerActivity::class.java)
                    startActivity(intent)
                }
                R.id.mainLogoutButton -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.Toast
//import androidx.appcompat.app.ActionBar
//import androidx.appcompat.widget.Toolbar
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.auth.FirebaseAuth
//
//class MainActivity : AppCompatActivity() {
//
//    private val TAG = "MainActivity"
//
//    private var auth = FirebaseAuth.getInstance()
//
//    val toolbar: Toolbar = findViewById(R.id.toolbar)
//
//    private lateinit var logoutButton: Button
//    private lateinit var friendButton: Button
//    private lateinit var updatePasswordButton: Button
//    private lateinit var cameraButton: Button
//    private lateinit var viewerButton: Button
//    private lateinit var startEventButton: Button
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        setSupportActionBar(toolbar)
//
//        val actionbar: ActionBar? = supportActionBar
//        actionbar?.apply {
//            setDisplayHomeAsUpEnabled(true)
//            setHomeAsUpIndicator(R.drawable.ic_menu)
//        }
//
//        friendButton = findViewById(R.id.mainFriendsButton)
//        logoutButton = findViewById(R.id.mainLogoutButton)
//        updatePasswordButton = findViewById(R.id.mainUpdatePasswordButton)
//        cameraButton = findViewById(R.id.mainCameraButton)
//        viewerButton = findViewById(R.id.mainViewerButton)
//        startEventButton = findViewById(R.id.mainStartEventButton)
//
////        db.collection("users")
////            .whereEqualTo("username", "Diddily")
////            .get()
////            .addOnSuccessListener { documents ->
////                for (document in documents) {
////                    usernameTextView.append(document.data?.get("username").toString())
////                    Log.d(TAG, "${document.id} => ${document.data}")
////                }
////            }
////            .addOnFailureListener { exception ->
////                Log.w(TAG, "Error getting documents: ", exception)
////            }
////
////        docRef.get()
////            .addOnSuccessListener { document ->
////                if (document != null) {
////                    usernameTextView.append(document.data?.get("username").toString())
////                    Log.d(TAG, "DocumentSnapshot data: ${document.data?.get("username").toString()}")
////                } else {
////                    Log.d(TAG, "No such document")
////                }
////            }
////            .addOnFailureListener { exception ->
////                Log.d(TAG, "get failed with ", exception)
////            }
////
////        val test = IDList(listOf("dwDJukLt3ygOBdiB1gINpYlp6Hy1", "jeKAzAngPEMWtfJxOoQQmpDnF1A3"))
////        db.collection("users").document(user?.uid.toString()).collection("requests").document("friends").update("friends", FieldValue.arrayUnion("TOCrhNM9wGXS96jTp7iplh5y0bw1"))
////
////        val docRef2 = db.collection("users").document(user?.uid.toString()).collection("requests").document("friends")
////        docRef2.get()
////            .addOnSuccessListener { document ->
////                val list = document.toObject(IDList::class.java)?.friends
////                if (document != null) {
////                    for (item in list!!){
////                        Log.d(TAG, item)
////                    }
////                    Log.d(TAG, document.data.toString())
////                } else {
////                    Log.d(TAG, "No such document")
////                }
////            }
////            .addOnFailureListener { exception ->
////                Log.d(TAG, "get failed with ", exception)
////            }
//
//        if (auth.currentUser == null) {
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        } else {
//            Toast.makeText(this, "Already logged in", Toast.LENGTH_LONG).show()
//        }
//
//
//        friendButton.setOnClickListener {
//            val intent = Intent(this, ConnectionActivity::class.java)
//            startActivity(intent)
//        }
//
//        logoutButton.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//
//        updatePasswordButton.setOnClickListener {
//            val intent = Intent(this, ResetPasswordActivity::class.java)
//            startActivity(intent)
//        }
//
//        cameraButton.setOnClickListener {
//            val intent = Intent(this, SenderActivity::class.java)
//            startActivity(intent)
//        }
//
//        viewerButton.setOnClickListener {
//            val intent = Intent(this, ViewerActivity::class.java)
//            startActivity(intent)
//        }
//
//        startEventButton.setOnClickListener {
//            val intent = Intent(this, AssignFriendActivity::class.java)
//            startActivity(intent)
//        }
//    }
//
//}
