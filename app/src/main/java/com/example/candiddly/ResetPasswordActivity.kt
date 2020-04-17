package com.example.candiddly

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var passwordEditText: EditText

    private lateinit var changePasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        auth = FirebaseAuth.getInstance()

        passwordEditText = findViewById(R.id.resetPasswordNewPasswordEditText)

        changePasswordButton = findViewById(R.id.resetPasswordChangePasswordButton)

        changePasswordButton.setOnClickListener{
            val password: String = passwordEditText.text.toString()
            if (TextUtils.isEmpty(password)) {
                displayError("Please enter password", "#32CD32")
            } else {
                auth.currentUser?.updatePassword(password)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            displayError("Password changed successfully", "#32CD32")
                            finish()
                        } else {
                            displayError("Password not changed", "#ffcc0000")
                        }
                    }
            }
        }
    }

    private fun displayError(message: String, color: String) {
        messageTextView.text = message
        messageTextView.setTextColor(Color.parseColor(color))
    }
}
