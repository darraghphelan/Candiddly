package com.example.candiddly

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CameraActivity : AppCompatActivity() {

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

        photoButton = findViewById(R.id.mainCameraButton)
        photoButton.setOnClickListener(photoListener)
    }

    private val photoListener = View.OnClickListener { view ->
        val receiverID: String = intent.getStringExtra("ReceiverID").toString()

            cameraKitView!!.captureImage { _, capture ->
                try {
                    @SuppressLint("SimpleDateFormat")
                    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
                    val currentDate = sdf.format(Date())

                    val imagesRef = storageRef.child("${user?.uid.toString()}/${currentDate}.jpg")

                    val bitmap = BitmapFactory.decodeByteArray(capture, 0, capture.size)
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)

                    val photo = baos.toByteArray()

                    val uploadTask = imagesRef.putBytes(photo)
                    uploadTask
                        .addOnSuccessListener {
                            imagesRef.downloadUrl.addOnSuccessListener { downloadURL ->
                                db.collection("users")
                                    .document(receiverID)
                                    .collection("gallery")
                                    .document("images")
                                    .update("images", FieldValue.arrayUnion("$downloadURL"))
                            }
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