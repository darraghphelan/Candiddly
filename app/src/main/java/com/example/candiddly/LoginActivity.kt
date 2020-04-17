package com.example.candiddly

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var loginTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.loginEmailEditText)
        passwordEditText = findViewById(R.id.loginPasswordEditText)
        loginButton = findViewById(R.id.loginLoginButton)
        forgotPasswordTextView = findViewById(R.id.loginForgotPasswordTextView)
        loginTextView = findViewById(R.id.loginRegisterTextView)

        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            messageTextView.text = ""
            val email: String = emailEditText.text.toString()
            val password: String = passwordEditText.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                displayMessage("Please fill all the fields", "#ffcc0000")
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if(task.isSuccessful) {
                        displayMessage("Successfully Logged In", "#32CD32")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else {
                        displayMessage("Login Failed", "#ffcc0000")
                    }
                }
            }
        }

        loginTextView.setOnClickListener{
            messageTextView.text = ""
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordTextView.setOnClickListener {
            messageTextView.text = ""
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java)) }
    }

    private fun displayMessage(message: String, color: String) {
        messageTextView.text = message
        messageTextView.setTextColor(Color.parseColor(color))
    }
}
