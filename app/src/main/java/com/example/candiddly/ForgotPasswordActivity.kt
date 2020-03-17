package com.example.candiddly

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var emailEditText: EditText

    private lateinit var resetPasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.forgotPasswordEmailEditText)

        resetPasswordButton = findViewById(R.id.forgotPasswordSendEmailButton)

        resetPasswordButton.setOnClickListener {
            val email: String = emailEditText.text.toString()
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter email id", Toast.LENGTH_LONG).show()
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            displayError("Reset link sent to your email","#32CD32")
                        } else {
                            displayError("Unable to send reset mail","#ffcc0000")
                        }
                    }
            }
        }
    }

    private fun displayError(message: String, color: String) {
        errorTextView.text = message
        errorTextView.setTextColor(Color.parseColor(color))
    }
}
