package com.example.prakiirti


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class StatusAdapter(private val statusList: ArrayList<Status>) :
    RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {


    class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.card_title2)
        val phase: TextView = itemView.findViewById(R.id.card_status)

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.status, parent, false)
        return StatusViewHolder(view)
    }


    override fun getItemCount(): Int {
        return statusList.size
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        holder.title.text=statusList[position].title
        holder.phase.text=statusList[position].phase
    }

}