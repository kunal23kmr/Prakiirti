package com.example.prakiirti


import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

//also used for add new post(blog)

class SuggestPage : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var story: TextInputLayout
    private lateinit var titleView: TextInputLayout
    private lateinit var suggest: Button
    private lateinit var camera: ImageButton
    private lateinit var gallery: ImageButton
    private var storageRef = Firebase.storage
    private lateinit var currentUser: FirebaseAuth
    private lateinit var uri: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggest_page)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        story = findViewById(R.id.story)
        titleView = findViewById(R.id.title)
        imageView = findViewById(R.id.image_pick)
        camera = findViewById(R.id.camera_pick)
        gallery = findViewById(R.id.gallery_pick)
        suggest = findViewById(R.id.suggest_btn)

        storageRef = FirebaseStorage.getInstance()
        currentUser = Firebase.auth
        uri = Uri.EMPTY

        val blog = intent.getParcelableExtra<Blog>("blog")
        if (blog != null) {
            story.editText?.setText(blog.story.toString())
            titleView.editText?.setText(blog.title.toString())
            Glide.with(this).load(blog.image).into(imageView)//for image
            supportActionBar?.title = blog.title
            uri= blog.image!!.toUri()
        }

        camera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 123)
        }

        gallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 456)

        }
        //image selection from gallery or, camera
        suggest.setOnClickListener {

            if ((titleView.editText?.text?.trim()
                    ?.isNotEmpty() == true) && (story.editText?.text?.trim()
                    ?.isNotEmpty() == true) && (uri != Uri.EMPTY)
            ) {
                val uid = currentUser.uid.toString()

                storageRef.getReference("images").child(uid)
                    .child(titleView.editText?.text.toString()).putFile(uri)
                    .addOnSuccessListener { task ->
                        task.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener {
                                val url = it.toString()
                                val suggestionTitle = titleView.editText?.text.toString()
                                val currentUser = Firebase.auth
                                val userReference =
                                    FirebaseDatabase.getInstance().getReference("user")
                                val blogReference =
                                    FirebaseDatabase.getInstance().getReference("blog")
                                val statusReference =
                                    FirebaseDatabase.getInstance().getReference("status")
                                val suggestionStory = story.editText?.text.toString()

                                userReference.child(currentUser.uid.toString()).get()
                                    .addOnSuccessListener { user ->
                                        val usernameDB = user.child("username").value.toString()
                                        val isAdmin = user.child("admin").value.toString().toInt()
                                        val setBlog =
                                            Blog(url, suggestionTitle, usernameDB, suggestionStory,uid)
                                        if (isAdmin == 1) {
                                            blogReference.child("showToAll").child(suggestionTitle)
                                                .setValue(setBlog)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        this@SuggestPage,
                                                        "Added successfully",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        } else {
                                            blogReference.child("suggestion").child(suggestionTitle)
                                                .setValue(setBlog)
                                                .addOnSuccessListener {
                                                    blogReference.child("suggestion")
                                                        .child(suggestionTitle)
                                                        .child("uid").setValue(uid)
                                                    statusReference.child(uid)
                                                        .child(suggestionTitle).setValue(
                                                        Status(
                                                            suggestionTitle,
                                                            "Pending"
                                                        )
                                                    )
                                                    Toast.makeText(
                                                        this@SuggestPage,
                                                        "Suggestion Queued",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        }
                                    }
                            }
                        Toast.makeText(
                            this@SuggestPage,
                            "After process blog will be added",
                            Toast.LENGTH_LONG
                        ).show()
                    }.addOnFailureListener {
                        Toast.makeText(
                            this@SuggestPage,
                            "Please select a file",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
            } else {
                Toast.makeText(
                    this@SuggestPage,
                    "Tile, Image and story must have some value",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (data?.extras?.get("data") != null) {
                val bmp = data.extras?.get("data") as Bitmap
                uri = saveImage(bmp)
                imageView.setImageURI(uri)
            }
        } else if (requestCode == 456) {
            if (data?.data != null) {
                imageView.setImageURI(data.data)
                uri = data.data!!
            }
        }
    }

    private fun saveImage(image: Bitmap): Uri {
        val imagesFolder = File(this@SuggestPage.cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "captured_image.jpg")
            val stream = FileOutputStream((file))
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(
                applicationContext,
                "com.example.prakiirti" + ".provider",
                file
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return uri!!
    }

}
