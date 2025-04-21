package com.example.bookingmedicalapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.model.Comment

class CommentAdapter(private val comments: List<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvRole: TextView = itemView.findViewById(R.id.tv_role)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val rvReplies: RecyclerView = itemView.findViewById(R.id.rv_replies)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.tvName.text = comment.name
        holder.tvRole.text = comment.role
        holder.tvTime.text = comment.time // Thời gian đã được format trong callApiComment
        holder.ratingBar.rating = comment.rating
        holder.tvDescription.text = comment.description

        holder.rvReplies.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvReplies.adapter = ReplyAdapter(comment.replies)
        holder.rvReplies.isNestedScrollingEnabled = false
    }

    override fun getItemCount(): Int = comments.size
}