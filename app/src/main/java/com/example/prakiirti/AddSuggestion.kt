package com.example.prakiirti

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase


//admins are adding the suggestion

class AddSuggestion : AppCompatActivity() {
    private lateinit var accept: Button
    private lateinit var reject: Button
    private lateinit var textView: TextView
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_suggestion)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        accept = findViewById(R.id.accept_btn)
        reject = findViewById(R.id.reject_btn)
        textView = findViewById(R.id.textView)
        imageView = findViewById(R.id.imageView)

        val blog = intent.getParcelableExtra<Blog>("blog")
        if (blog != null) {
            textView.text = blog.story
            supportActionBar?.title = blog.title
            Glide.with(this).load(blog.image).into(imageView)//for image
        }

        val blogReference = FirebaseDatabase.getInstance().getReference("blog")
        val statusReference = FirebaseDatabase.getInstance().getReference("status")
        val status = Status(blog?.title.toString(), "Pending")
        accept.setOnClickListener {
            status.phase = "Accepted"
            blogReference.child("showToAll").child(blog?.title.toString()).setValue(blog)
            blogReference.child("suggestion").child(blog?.title.toString()).setValue(null)
            statusReference.child(blog?.uid.toString()).child(blog?.title.toString())
                .setValue(status)
        }
        reject.setOnClickListener {
            status.phase = "Rejected"
            blogReference.child("suggestion").child(blog?.title.toString()).setValue(null)
            statusReference.child(blog?.uid.toString()).child(blog?.title.toString())
                .setValue(status)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}