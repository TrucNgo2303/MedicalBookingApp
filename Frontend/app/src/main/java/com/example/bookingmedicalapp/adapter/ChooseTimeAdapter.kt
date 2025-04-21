package com.example.bookingmedicalapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bookingmedicalapp.R

class ChooseTimeAdapter(
    private val timeSlots: List<String>,
    private val onTimeSelected: (String) -> Unit
    ) : RecyclerView.Adapter<ChooseTimeAdapter.ChooseTimeViewHolder>() {

    private var selectedPosition = 0

    inner class ChooseTimeViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val timeText: TextView = view.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseTimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_choose_time, parent, false)
        return ChooseTimeViewHolder(view)
    }

    override fun getItemCount() : Int = timeSlots.size

    override fun onBindViewHolder(holder: ChooseTimeViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val time = timeSlots[position]
        holder.timeText.text = time

        val isSelected = position == selectedPosition

        // Đổi màu chữ
        holder.timeText.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (isSelected) android.R.color.white else R.color.gray
            )
        )

        // Đổi màu nền
        holder.timeText.setBackgroundResource(
            if (isSelected) R.drawable.bg_choose_time_selected else R.drawable.bg_choose_time_default
        )

        holder.itemView.setOnClickListener {
            val previous = selectedPosition
            selectedPosition = position
            notifyItemChanged(previous)
            notifyItemChanged(selectedPosition)
            onTimeSelected(time)
        }
    }

}