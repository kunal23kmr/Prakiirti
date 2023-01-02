package com.example.prakiirti

import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

//for admin to see the all suggestions

class SuggestionList : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var blogList: ArrayList<Blog>
    private lateinit var blogAdapter: BlogAdapter
    private lateinit var database: DatabaseReference


    var context = this
    var connectivity: ConnectivityManager? = null
    private var info: NetworkInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestion_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = "New Suggestions"

        recyclerView = findViewById(R.id.user_main_recyclerView)
        blogList = arrayListOf()
        blogAdapter= BlogAdapter(blogList,context)
        recyclerView.adapter = blogAdapter

        connectivity = context.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            info = connectivity!!.activeNetworkInfo
            if (info!!.state == NetworkInfo.State.CONNECTED) {
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = LinearLayoutManager(this)

                database = FirebaseDatabase.getInstance().getReference("blog").child("suggestion")
                database.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (dataSnapShot in snapshot.children) {
                                val blog = dataSnapShot.getValue(Blog::class.java)
                                if (!blogList.contains(blog)) {
                                    blogList.add(blog!!)
                                }
                            }
                            blogAdapter = BlogAdapter(blogList, this@SuggestionList)
                            recyclerView.adapter = blogAdapter
                            blogAdapter.onItemClick = {
                                val intent = Intent(this@SuggestionList, AddSuggestion::class.java)
                                intent.putExtra("blog", it)
                                startActivity(intent)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@SuggestionList, error.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                })

            } else {
                Toast.makeText(this, "No Internet!!", Toast.LENGTH_LONG).show()
            }
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}