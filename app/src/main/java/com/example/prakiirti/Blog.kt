package com.example.prakiirti

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


data class Blog(
    val image: String? = null,
    val title: String? = null,
    val username: String? = null,
    val story: String? = null,
    val uid: String? = null
) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()
    )

//    fun requestUploadBlog(blog: Blog) {
//        val currentUser = Firebase.auth
//        val reference = FirebaseDatabase.getInstance().getReference("blog")
//        reference.child("suggestions").child(currentUser.uid.toString())
//            .child(blog.title.toString())
//            .setValue(blog)
//    }
//
//    fun addBlog(blog: Blog) {
//        val reference = FirebaseDatabase.getInstance().getReference("blog")
//        reference.child("showToAll").child(blog.title.toString()).setValue(blog)
//    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

        parcel.writeString(image)
        parcel.writeString(title)
        parcel.writeString(username)
        parcel.writeString(story)
        parcel.writeString(uid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Blog> {
        override fun createFromParcel(parcel: Parcel): Blog {
            return Blog(parcel)
        }

        override fun newArray(size: Int): Array<Blog?> {
            return arrayOfNulls(size)
        }
    }
}
