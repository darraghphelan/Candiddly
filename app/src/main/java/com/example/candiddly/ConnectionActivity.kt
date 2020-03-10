package com.example.candiddly

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_connections.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ConnectionActivity : AppCompatActivity() {
    private val TAG = "ConnectionActivity"

    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    private var idList = listOf<String>()
    private var connectionList = mutableListOf<User>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connections)
        recyclerFriendsButton.isEnabled = false
        recyclerFriendsButton.isClickable = false
        lifecycleScope.launch {
            initView()
        }

        itemsswipetorefresh.setOnRefreshListener {
            when {
                !recyclerFriendsButton.isEnabled -> {
                    recyclerFriendsButton.performClick()
                }
                !recyclerReceivedButton.isEnabled -> {
                    recyclerReceivedButton.performClick()
                }
                !recyclerSentButton.isEnabled -> {
                    recyclerSentButton.performClick()
                }
            }
            itemsswipetorefresh.isRefreshing = false
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
        }
    }

    private suspend fun initView() {
        recyclerViewFriends.layoutManager = LinearLayoutManager(this)
        recyclerViewFriends.addItemDecoration(VerticalSpaceItemDecoration(48))
        recyclerViewFriends.addItemDecoration(DividerItemDecoration(this))
        getData("friends")
    }

    private suspend fun getData(requestType: String){
        val friendListAdapter = ConnectionListHeaderAdapter()
        recyclerViewFriends.adapter = friendListAdapter
        friendListAdapter.setFriendList(generateData(requestType))
    }

    private suspend fun generateData(requestType: String): List<User> {
        connectionList = getConnectionList(requestType)
        return connectionList
    }

    private suspend fun getConnectionList(requestType: String): MutableList<User> {
        connectionList.clear()
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
            .document(user?.uid.toString())
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
}