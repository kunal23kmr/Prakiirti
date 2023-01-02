package com.example.prakiirti

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class SuggestionStatus : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var statusList: ArrayList<Status>
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestion_status)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title="Suggestion status"

        recyclerView = findViewById(R.id.status_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        statusList = arrayListOf()

        reference = FirebaseDatabase.getInstance().getReference("status")
            .child(Firebase.auth.currentUser?.uid.toString())
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dataSnapShot in snapshot.children) {
                        val status = dataSnapShot.getValue(Status::class.java)
                        if (!statusList.contains(status)) {
                            statusList.add(status!!)
                        }
                    }
                    recyclerView.adapter = StatusAdapter(statusList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SuggestionStatus, error.toString(), Toast.LENGTH_SHORT).show()
            }
        })
        recyclerView.adapter = StatusAdapter(statusList)


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}