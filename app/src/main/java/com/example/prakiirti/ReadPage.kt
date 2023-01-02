package com.example.prakiirti

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class ReadPage : AppCompatActivity() {

    private lateinit var delete: Button

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_page)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val textView: TextView = findViewById(R.id.blog_title)
        val imageView: ImageView = findViewById(R.id.imageRead)

        delete = findViewById(R.id.deletebtn)
        delete.isEnabled = false
        delete.isVisible = false

        val blog = intent.getParcelableExtra<Blog>("blog")
        if (blog != null) {
            textView.text = blog.story
            supportActionBar?.title = blog.title
            Glide.with(this).load(blog.image).into(imageView)//for image
        }

        val currentUser = Firebase.auth
        val userReference = FirebaseDatabase.getInstance().getReference("user")
        userReference.child(currentUser.uid.toString()).get()
            .addOnSuccessListener { user ->
                val isAdmin = user.child("admin").value.toString().toInt()
                if (isAdmin == 1) {
                    delete.isEnabled = true
                    delete.isVisible = true
                }
            }
        val blogReference = FirebaseDatabase.getInstance().getReference("blog")

        findViewById<Button>(R.id.button).setOnClickListener {
            val intent = Intent(this, SuggestPage::class.java)
            intent.putExtra("blog", blog)
            startActivity(intent)
        }

        delete.setOnClickListener{
            deleteBlog(blogReference,blog)
        }



    }

    private fun deleteBlog(blogReference: DatabaseReference, blog: Blog?) {
        blogReference.child("showToAll").child(blog?.title.toString()).removeValue()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}