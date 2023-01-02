package com.example.prakiirti

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private lateinit var userEmail: TextInputLayout
    private lateinit var username: TextInputLayout
    private lateinit var password: TextInputLayout
    private lateinit var auth: FirebaseAuth
    private var isAdmin: Boolean = false

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                val intent =
                    Intent(this@Login, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                currentUser.sendEmailVerification()
                auth.signOut()
                Toast.makeText(
                    this@Login,
                    "E-mail not verified check your email",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //This was to remove task bar
        userEmail = findViewById(R.id.log_email)
        password = findViewById(R.id.password)
        auth = Firebase.auth
        username = findViewById(R.id.log_username)

        onStart()

        findViewById<Button>(R.id.signup_screen).setOnClickListener {
            startActivity(
                Intent(this, Register::class.java)
            )
        }

        findViewById<Button>(R.id.forget_pass_button).setOnClickListener {
            startActivity(Intent(this, ForgetPassword::class.java))
        }

        //starting a new activity....
        findViewById<Button>(R.id.sign_in_button).setOnClickListener {
            if (validateUserEmail() && validatePassword()) {
                val userEnteredEmail = userEmail.editText?.text.toString()
                val userEnteredPassword = password.editText?.text.toString().trim()

                auth.signInWithEmailAndPassword(userEnteredEmail, userEnteredPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            Toast.makeText(
                                baseContext, "Signed-in with email",
                                Toast.LENGTH_SHORT
                            ).show()

                            if (auth.currentUser?.isEmailVerified == true) {
                                if (validateUsername()) {
                                    val intent = Intent(this@Login, MainActivity::class.java)
                                    intent.putExtra("isAdmin?", isAdmin)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    username.error = "Wrong user name"
                                    auth.signOut()
                                }
                            } else {
                                auth.currentUser?.sendEmailVerification()
                                auth.signOut()
                                Toast.makeText(
                                    this@Login,
                                    "E-mail not verified\ncheck your email",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }.addOnFailureListener{
                        password.error="Invalid password"
                        password.isErrorEnabled=true
                    }
            }
        }
    }

    private fun validateUsername(): Boolean {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("user")
        val username: String =
            reference.child(auth.currentUser?.uid.toString())?.child("username")?.get().toString()
        return (username != null)
    }

    private fun validateUserEmail(): Boolean {
        val value = userEmail.editText?.text.toString()

        return if (value.isEmpty()) {
            userEmail.error = "Field can't be empty"
            false
        } else {
            userEmail.error = null
            userEmail.isErrorEnabled = false
            true
        }
    }

    private fun validatePassword(): Boolean {
        val value = password.editText?.text.toString()

        return if (value.isEmpty()) {
            password.error = "Field can't be empty"
            false
        } else {
            password.error = null
            password.isErrorEnabled = false
            true
        }
    }
}
