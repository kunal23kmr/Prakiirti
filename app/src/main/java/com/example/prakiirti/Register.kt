package com.example.prakiirti

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        window.setFlags(
            FLAG_FULLSCREEN,
            FLAG_FULLSCREEN
        )
        //This was to remove task bar

        auth = Firebase.auth

        val regname = findViewById<TextInputLayout>(R.id.reg_name)
        val regUsername = findViewById<TextInputLayout>(R.id.reg_username)
        val regEmail = findViewById<TextInputLayout>(R.id.reg_email)
        val regPass = findViewById<TextInputLayout>(R.id.reg_password)
        val regBtn = findViewById<Button>(R.id.reg_btn)
        val regToLoginBtn = findViewById<Button>(R.id.reg_to_login)

        var rootNode: FirebaseDatabase
        var reference: DatabaseReference


        regToLoginBtn.setOnClickListener {
            finish()
        }

        fun validateName(): Boolean {
            val value = regname.editText?.text.toString()

            return if (value.isEmpty()) {
                regname.error = "Field can't be empty"
                false
            } else {
                regname.error = null
                regname.isErrorEnabled = false
                true
            }
        }

        fun validateUsername(): Boolean {
            val value = regUsername.editText?.text.toString()
            val noWhiteSpace =
                "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]\$".toRegex()

            return if (value.isEmpty()) {
                regUsername.error = "Field can't be empty"
                false
            } else if (value.length >= 20) {
                regUsername.error = "Username too long"
                false
            } else if (!value.matches(noWhiteSpace)) {
                regUsername.error = "White spaces are not allowed"
                false
            } else {
                regUsername.error = null
                regUsername.isErrorEnabled = false
                true
            }
        }

        fun validateEmail(): Boolean {
            val value = regEmail.editText?.text.toString()


            return if (value.isEmpty()) {
                regEmail.error = "Field can't be empty"
                false
            } else {
                regEmail.error = null
                regEmail.isErrorEnabled = false
                true
            }
        }

        fun validatePassword(): Boolean {
            val value = regPass.editText?.text.toString()
            val passwordVal = "^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$"

            return if (value.isEmpty()) {
                regPass.error = "Field can't be empty"
                false
            } else if (!value.matches(passwordVal.toRegex())) {
                regPass.error = "Password too weak"
                false
            } else {
                regPass.error = null
                regPass.isErrorEnabled = false
                true
            }
        }


        regBtn.setOnClickListener {
            if (validateName() && validateUsername() && validateEmail() && validatePassword()) {
                rootNode = FirebaseDatabase.getInstance()
                reference = rootNode.getReference("user")
                //get all the values
                val name = regname.editText?.text.toString()
                val username = regUsername.editText?.text.toString().trim()
                val email = regEmail.editText?.text.toString().trim()
                val password = regPass.editText?.text.toString().trim()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            regEmail.error = null
                            regEmail.isErrorEnabled = false

                            if (auth.currentUser == null) {
                                Toast.makeText(this, "User nahi bana", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this, "User ban gaya", Toast.LENGTH_LONG).show()
                            }
                            auth.currentUser?.sendEmailVerification()
                                ?.addOnSuccessListener {
                                    Log.d("TAG", "createUserWithEmail:success")
                                    Toast.makeText(
                                        this,
                                        "Check E-mail and verify",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    reference.child(auth.currentUser?.uid.toString())
                                        .setValue(UserHelperClass(name, username, email, password))
                                        .addOnSuccessListener {

                                            regname.editText?.text?.clear()
                                            regUsername.editText?.text?.clear()
                                            regEmail.editText?.text?.clear()
                                            regPass.editText?.text?.clear()

                                        }.addOnFailureListener {
                                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                ?.addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Unable to verify email",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    auth.currentUser?.delete()
                                }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            regEmail.error = "This E-mail is already registered, please login."
                        }
                    }
            }
        }
    }
}