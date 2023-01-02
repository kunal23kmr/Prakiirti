package com.example.prakiirti

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        supportActionBar?.title="Forget Password"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val email=findViewById<TextView>(R.id.editEmailAddress)
        val btn=findViewById<Button>(R.id.verify)
        val auth=Firebase.auth
        btn.setOnClickListener{
            if(email.text!=null){
                auth.sendPasswordResetEmail(email.text.toString())
                    .addOnSuccessListener {
                        Toast.makeText(this,"Check your email",Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this,"Please enter a valid email",Toast.LENGTH_LONG).show()
                    }
            }else{
                Toast.makeText(this,"Please enter the email",Toast.LENGTH_LONG).show()
            }
        }




    }
}