package com.example.candiddly

import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.loginEmailEditText)
        passwordEditText = findViewById(R.id.loginPasswordEditText)

        loginButton = findViewById(R.id.loginLoginButton)

        forgotPasswordTextView = findViewById(R.id.loginForgotPasswordTextView)

        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            errorTextView.text = ""
            val email: String = emailEditText.text.toString()
            val password: String = passwordEditText.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                displayError("Please fill all the fields")
            } else{
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if(task.isSuccessful) {
                        displayError("Successfully Logged In")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else {
                        displayError("Login Failed")
                    }
                }
            }
        }

        loginRegisterTextView.setOnClickListener{
            errorTextView.text = ""
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        forgotPasswordTextView.setOnClickListener {
            errorTextView.text = ""
            startActivity(Intent(this@LoginActivity,
                ForgotPasswordActivity::class.java)) }
    }

    private fun displayError(message: String) {
        errorTextView.text = message

    }
}
