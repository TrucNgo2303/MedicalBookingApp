package com.example.bookingmedicalapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.model.Reply

class ReplyAdapter(private val replies: List<Reply>) : RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder>() {
    inner class ReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvRole: TextView = itemView.findViewById(R.id.tv_role)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reply, parent, false)
        return ReplyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        val reply = replies[position]
        holder.tvName.text = reply.name
        holder.tvRole.text = reply.role
        holder.tvTime.text = reply.time // Thời gian đã được format trong callApiReply
        holder.tvDescription.text = reply.description
    }

    override fun getItemCount(): Int = replies.size
}