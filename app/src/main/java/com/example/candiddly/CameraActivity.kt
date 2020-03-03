package com.example.candiddly

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.camerakit.CameraKitView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CameraActivity : AppCompatActivity() {

    private val TAG = "CameraActivity"

    private var cameraKitView: CameraKitView? = null

    private lateinit var photoButton: FloatingActionButton

    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        cameraKitView = findViewById(R.id.camera)

        photoButton = findViewById(R.id.photoButton)
        photoButton.setOnClickListener(photoListener)
    }

    private val photoListener = View.OnClickListener { view ->
            cameraKitView!!.captureImage { cameraKitView, photo ->
                //val savedPhoto = File(Environment.getExternalStorageDirectory(), "photo.jpg")
                try {
                    @SuppressLint("SimpleDateFormat")
                    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
                    val currentDate = sdf.format(Date())

                    val testImagesRef = storageRef.child("${user?.uid.toString()}/${currentDate}.jpg")

                    val uploadTask = testImagesRef.putBytes(photo)
                    uploadTask
                        .addOnSuccessListener {
                            testImagesRef.downloadUrl.addOnSuccessListener { downloadURL ->
                                db.collection("users").document(user?.uid.toString()).collection("gallery").document("images").update("images", FieldValue.arrayUnion("$downloadURL"))
                            }
                    }
                        .addOnFailureListener {
                            Log.d(TAG, "onFailureListener activated")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e("CKDemo", "Exception in photo callback")
                }
            }
        }

    override fun onResume() {
        super.onResume()
        cameraKitView!!.onResume()
    }

    override fun onPause() {
        cameraKitView!!.onPause()
        super.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraKitView!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}