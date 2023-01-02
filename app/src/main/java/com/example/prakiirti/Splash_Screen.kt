package com.example.prakiirti

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Splash_Screen : AppCompatActivity() {
    lateinit var handler: Handler//for move screen(variable creation)
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        auth = Firebase.auth

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //This was to remove task bar

        findViewById<TextView>(R.id.app_name).animation =
            AnimationUtils.loadAnimation(this, R.anim.name_animation)
        //for move_yahan se
        handler = Handler()
        handler.postDelayed(
            {
                if (auth.currentUser != null) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                }
                finish()
            },
            5500
        )
        //end_move yahan tak
    }
}