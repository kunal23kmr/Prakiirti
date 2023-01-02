package com.example.prakiirti

import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var blogList: ArrayList<Blog>
    private lateinit var blogAdapter: BlogAdapter
    private lateinit var database: DatabaseReference
    private lateinit var addBlog: FloatingActionButton
    private lateinit var launchIntent: Intent
    var context = this
    var connectivity: ConnectivityManager? = null
    var info: NetworkInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        supportActionBar?.title = "Prakiirti"

        blogList = arrayListOf()
        recyclerView = findViewById(R.id.user_main_recyclerView)
        blogAdapter = BlogAdapter(blogList, this@MainActivity)
        recyclerView.adapter = blogAdapter
        addBlog = findViewById(R.id.add_blog_btn)
        launchIntent=intent

        val currentUser = Firebase.auth
        val userReference = FirebaseDatabase.getInstance().getReference("user")
        userReference.child(currentUser.uid.toString()).get()
            .addOnSuccessListener { user ->
                val isAdmin = user.child("admin").value.toString().toInt()
                launchIntent = if (isAdmin == 1) {
                    Intent(this@MainActivity, SuggestionList::class.java)
                } else {
                    Intent(this@MainActivity, SuggestionStatus::class.java)
                }
            }

        connectivity = context.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            info = connectivity!!.activeNetworkInfo
            if (info?.state == NetworkInfo.State.CONNECTED) {

                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = LinearLayoutManager(this)

                database = FirebaseDatabase.getInstance().getReference("blog").child("showToAll")
                database.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (dataSnapShot in snapshot.children) {
                                val blog = dataSnapShot.getValue(Blog::class.java)
                                if (!blogList.contains(blog)) {
                                    blogList.add(blog!!)
                                }
                            }
                            blogAdapter = BlogAdapter(blogList, this@MainActivity)
                            recyclerView.adapter = blogAdapter
                            blogAdapter.onItemClick = {
                                val intent = Intent(this@MainActivity, ReadPage::class.java)
                                intent.putExtra("blog", it)
                                startActivity(intent)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@MainActivity, error.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                })

            } else {
                addBlog.isEnabled = false
                launchIntent = intent
                Toast.makeText(this, "No Internet!!", Toast.LENGTH_LONG).show()
            }
        }

        addBlog.setOnClickListener {
            val intent = Intent(this, SuggestPage::class.java)
            startActivity(intent)
        }
    }

    //action bar control
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.logout -> logout()
            R.id.status -> if (launchIntent != intent) {
                startActivity(launchIntent)
            } else {
                startActivity(launchIntent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        Firebase.auth.signOut()
        startActivity(Intent(this@MainActivity, Login::class.java))
        finish()
    }
}