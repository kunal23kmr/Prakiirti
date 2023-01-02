package com.example.prakiirti

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BlogAdapter(private val blogList: ArrayList<Blog>, private val context: Context) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    var onItemClick: ((Blog) -> Unit)? = null

    class BlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.card_image)
        val title: TextView = itemView.findViewById(R.id.card_title)
        val username: TextView = itemView.findViewById(R.id.card_userName)

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_recycle, parent, false)
        return BlogViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blog = blogList[position]

        Glide.with(context).load(blog.image).into(holder.imageView)//for image
        holder.title.text = blog.title
        holder.username.text = blog.username

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(blog)
        }

    }

    override fun getItemCount(): Int {
        return blogList.size
    }

}