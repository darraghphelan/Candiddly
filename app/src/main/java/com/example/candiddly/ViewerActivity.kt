package com.example.candiddly

import classes.IDList
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_viewer.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ViewerActivity : AppCompatActivity() {

    private val TAG = "ViewerActivity"

    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    private var idList = listOf<String>()
    private var imageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewer)

        val imageView = findViewById<ImageView>(R.id.image)

        fun saveImage() {
            val externalStorageState = Environment.getExternalStorageState()
            if (externalStorageState == Environment.MEDIA_MOUNTED) {

                try {
                    val bitmapDrawable = imageView.drawable as BitmapDrawable
                    val bitmap = bitmapDrawable.bitmap
                    val storage = Environment.getExternalStorageDirectory()
                    val dir = File(storage.absolutePath + "/Candiddly")
                    dir.mkdirs()
                    val fileName = String.format("%d.jpg", System.currentTimeMillis())
                    val outFile = File(dir, fileName)
                    val outStream = FileOutputStream(outFile)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                    outStream.flush()
                    outStream.close()

                    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    val contentUri = Uri.fromFile(outFile)
                    mediaScanIntent.data = contentUri
                    this.sendBroadcast(mediaScanIntent)

                    Toast.makeText(this, "Image saved to Candiddly folder", Toast.LENGTH_LONG).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(this, "Unable to access storage", Toast.LENGTH_LONG).show()

            }
        }

        viewerSaveButton.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
                } else {
                    saveImage()
                }
            } else {
                saveImage()
            }
        }

        fun glide(list: List<String>){
            if (list.isEmpty()) {
                imageView.setImageResource(0)
                return
            }
            imageUrl = list[0]

            Glide.with(this /* context */)
                .load(imageUrl)
                .into(imageView)

            val deleteRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
            Log.d(TAG, "deleteRef = $deleteRef")

            deleteRef.delete()
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully deleted $imageUrl")
            }
                .addOnFailureListener {
                    Log.d(TAG, "Failed deleting $imageUrl")
            }
            db.collection("users")
                .document(user?.uid.toString())
                .collection("gallery")
                .document("images")
                .update("images", FieldValue.arrayRemove(imageUrl))


            idList = idList.drop(1)
        }

        lifecycleScope.launch {
            idList = getImageUrls()
            glide(idList)
        }
        viewerNextButton.setOnClickListener{
            glide(idList)
        }
    }

    private suspend fun getImageUrls(): List<String>{
            val docRefImages = db.collection("users")
                .document(user?.uid.toString())
                .collection("gallery")
                .document("images")

            val document = docRefImages.get().await()
            idList = document.toObject(IDList::class.java)?.images!!
            return idList
    }
}
