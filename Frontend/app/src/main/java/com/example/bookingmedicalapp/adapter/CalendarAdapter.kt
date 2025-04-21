package com.example.bookingmedicalapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.model.DateModel

class CalendarAdapter(
    private val days: List<DateModel>,
    private var selectedDate: String,
    var onDateSelected: (String) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DateViewHolder>() {

    inner class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayText: TextView = view.findViewById(R.id.tv_day)
        val dateText: TextView = view.findViewById(R.id.tv_date)
        val itemLayout: LinearLayout = view.findViewById(R.id.item_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar, parent, false)
        return DateViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val dateModel = days[position]

        holder.dayText.text = dateModel.day
        holder.dateText.text = dateModel.date.toString()

        if (dateModel.fullDate == selectedDate) {
            holder.itemLayout.setBackgroundResource(R.drawable.bg_date_selected)
            holder.dayText.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
            holder.dateText.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
        } else {
            holder.itemLayout.setBackgroundResource(R.drawable.bg_date)
            holder.dayText.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
            holder.dateText.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
        }

        holder.itemView.setOnClickListener {
            selectedDate = dateModel.fullDate
            notifyDataSetChanged()
            onDateSelected(selectedDate)
        }
    }

    override fun getItemCount() = days.size
}