package com.example.candiddly

import android.content.Intent
import classes.*
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_connections.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ConnectionActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private var idList = listOf<String>()
    private var connectionList = mutableListOf<User>()
    private var friendList = mutableListOf<User>()
    private var receivedList = mutableListOf<User>()
    private var sentList = mutableListOf<User>()

    private lateinit var usernameEditText: EditText
    private lateinit var addFriendButton: Button


    private lateinit var adapter: ConnectionRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connections)

        usernameEditText = findViewById(R.id.connectionsUsernameEditText)
        addFriendButton = findViewById(R.id.connectionsAddFriendButton)

        recyclerFriendsButton.isEnabled = false
        recyclerFriendsButton.isClickable = false
        lifecycleScope.launch {
            initView()
            getConnectionList("received")
            getConnectionList("sent")
        }

        itemsswipetorefresh.setOnRefreshListener {
            when {
                !recyclerFriendsButton.isEnabled -> {
                    friendList.clear()
                    recyclerFriendsButton.performClick()
                }
                !recyclerReceivedButton.isEnabled -> {
                    receivedList.clear()
                    recyclerReceivedButton.performClick()
                }
                !recyclerSentButton.isEnabled -> {
                    sentList.clear()
                    recyclerSentButton.performClick()
                }
            }
            itemsswipetorefresh.isRefreshing = false
            errorTextView.text = ""
        }

        addFriendButton.setOnClickListener {
            errorTextView.text = ""
            val username = usernameEditText.text.toString()
            usernameEditText.text.clear()

            for (user in friendList) {
                if (user.username == username) {
                    displayMessage("${user.username} is already a friend", "#ffcc0000")
                    return@setOnClickListener
                }
            }

            for (user in sentList) {
                if (user.username == username) {
                    displayMessage("${user.username} friend request already sent", "#ffcc0000")
                    return@setOnClickListener
                }
            }

            for (user in receivedList) {
                if (user.username == username) {
                    displayMessage("Find the ${user.username} request in 'Received'", "#ffcc0000")
                    return@setOnClickListener
                }
            }

            lifecycleScope.launch {
                val docRefUsers = db.collection("users")
                val userDoc = docRefUsers
                    .whereEqualTo("username", username)
                    .get()
                    .await()
                if (userDoc.isEmpty) {
                    displayMessage("No user $username exists, please try again", "#ffcc0000")
                    return@launch
                }

                for (user in receivedList) {
                    if (user.username == username) {
                        db.collection("users")
                            .document(currentUser?.uid.toString())
                            .collection("connections")
                            .document("friends")
                            .update("friends", FieldValue.arrayUnion(user.id))

                        db.collection("users")
                            .document(user.id)
                            .collection("connections")
                            .document("friends")
                            .update("friends", FieldValue.arrayUnion(currentUser?.uid.toString()))

                        db.collection("users")
                            .document(currentUser?.uid.toString())
                            .collection("connections")
                            .document("received")
                            .update("received", FieldValue.arrayRemove(user.id))

                        db.collection("users")
                            .document(user.id)
                            .collection("connections")
                            .document("sent")
                            .update("sent", FieldValue.arrayRemove(currentUser?.uid.toString()))
                        return@launch
                    }
                }

                for (doc in userDoc) {
                        val user = doc.toObject(User::class.java)
                        db.collection("users")
                            .document(currentUser?.uid.toString())
                            .collection("connections")
                            .document("sent")
                            .update("sent", FieldValue.arrayUnion(user.id))

                        displayMessage("Sent user ${user.username} a friend request", "#32CD32")

                        db.collection("users")
                            .document(user.id)
                            .collection("connections")
                            .document("received")
                            .update("received", FieldValue.arrayUnion(currentUser?.uid.toString()))
                    }
            }
        }

        recyclerFriendsButton.setOnClickListener {
            lifecycleScope.launch {
                getData("friends")
            }
            recyclerFriendsButton.isEnabled = false
            recyclerFriendsButton.isClickable = false
            recyclerReceivedButton.isEnabled = true
            recyclerReceivedButton.isClickable = true
            recyclerSentButton.isEnabled = true
            recyclerSentButton.isClickable = true
            errorTextView.text = ""
        }
        recyclerReceivedButton.setOnClickListener {
            lifecycleScope.launch {
                getData("received")
            }
            recyclerReceivedButton.isEnabled = false
            recyclerReceivedButton.isClickable = false
            recyclerFriendsButton.isEnabled = true
            recyclerFriendsButton.isClickable = true
            recyclerSentButton.isEnabled = true
            recyclerSentButton.isClickable = true
            errorTextView.text = ""
        }
        recyclerSentButton.setOnClickListener {
            lifecycleScope.launch {
                getData("sent")
            }
            recyclerSentButton.isEnabled = false
            recyclerSentButton.isClickable = false
            recyclerFriendsButton.isEnabled = true
            recyclerFriendsButton.isClickable = true
            recyclerReceivedButton.isEnabled = true
            recyclerReceivedButton.isClickable = true
            errorTextView.text = ""
        }
    }

    private suspend fun initView() {
        connectionsRecyclerView.layoutManager = LinearLayoutManager(this)
        connectionsRecyclerView.addItemDecoration(VerticalSpaceItemDecoration(48))
        connectionsRecyclerView.addItemDecoration(DividerItemDecoration(this))
        getData("friends")
    }

    private suspend fun getData(requestType: String){
        val connectionList = getConnectionList(requestType)

        when (requestType) {
            "friends" -> {
                adapter = ConnectionRecyclerAdapter(connectionList) { user ->
                val productIntent = Intent(this, CameraActivity::class.java)
                productIntent.putExtra("ReceiverID", user.id)
                startActivity(productIntent)
                }
            }
            "received" -> {
                adapter = ConnectionRecyclerAdapter(connectionList) { user ->
                val intent = Intent(this, PopUpWindow::class.java)
                intent.putExtra("popuptext", "Do you want to accept this request?")
                    intent.putExtra("type", "received")
                    intent.putExtra("id", user.id)
                startActivity(intent)
                }
            }
            "sent" -> {
                adapter = ConnectionRecyclerAdapter(connectionList) { user ->
                val intent = Intent(this, PopUpWindow::class.java)
                intent.putExtra("popuptext", "Do you want to delete this request?")
                    intent.putExtra("type", "sent")
                    intent.putExtra("id", user.id)
                startActivity(intent)
                }
            }
        }

        connectionsRecyclerView.adapter = adapter
    }

    private suspend fun getConnectionList(requestType: String): MutableList<User> {
        if (requestType == "friends" && friendList.isNotEmpty()) {
            return friendList
        } else if (requestType == "received" && receivedList.isNotEmpty()) {
            return receivedList
        } else if (requestType == "sent" && sentList.isNotEmpty()) {
            return sentList
        }

        when (requestType) {
            "friends" -> {
                connectionList = friendList
            }
            "received" -> {
                connectionList = receivedList
            }
            "sent" -> {
                connectionList = sentList
            }
        }

        val docRefUsers = db.collection("users")
        val userDocs = docRefUsers
            .whereIn("id", getConnectionIdList(requestType))
            .get()
            .await()

        for (doc in userDocs) {
            val user = doc.toObject(User::class.java)
            connectionList.add(user)
        }
        return connectionList
    }

    private suspend fun getConnectionIdList(requestType: String): List<String>{
        val docRefFriends = db.collection("users")
            .document(currentUser?.uid.toString())
            .collection("connections")
            .document(requestType)
        val document = docRefFriends.get().await()
        when (requestType) {
            "friends" -> {
                idList = document.toObject(IDList::class.java)?.friends!!
            }
            "received" -> {
                idList = document.toObject(IDList::class.java)?.received!!
            }
            "sent" -> {
                idList = document.toObject(IDList::class.java)?.sent!!
            }
        }
        return idList
    }

    private fun displayMessage(message: String, color: String) {
        errorTextView.text = message
        errorTextView.setTextColor(Color.parseColor(color))
    }
}