package com.example.bookingmedicalapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bookingmedicalapp.R
import java.text.SimpleDateFormat
import java.util.*

class ChooseTimeAdapter(
    private val timeSlots: List<String>,
    private val bookedAppointments: List<String>, // Danh sách lịch hẹn (yyyy-MM-dd h:mm a)
    private val onTimeSelected: (String) -> Unit
) : RecyclerView.Adapter<ChooseTimeAdapter.ChooseTimeViewHolder>() {

    private var selectedPosition = -1 // Không chọn mặc định
    private var selectedDate: String? = null // Ngày được chọn từ CalendarAdapter

    inner class ChooseTimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeText: TextView = view.findViewById(R.id.tv_time)
    }

    fun updateSelectedDate(date: String) {
        selectedDate = date
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseTimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_choose_time, parent, false)
        return ChooseTimeViewHolder(view)
    }

    override fun getItemCount(): Int = timeSlots.size

    override fun onBindViewHolder(holder: ChooseTimeViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val time = timeSlots[position]
        holder.timeText.text = time

        // Chuẩn hóa định dạng giờ để so sánh
        val timeFormat = SimpleDateFormat("h:mm a", Locale("vi", "VN"))
        val parsedTime = try {
            timeFormat.parse(time)
        } catch (e: Exception) {
            null
        }
        val normalizedTime = parsedTime?.let { timeFormat.format(it) } ?: time

        // Kiểm tra nếu giờ đã được đặt cho ngày được chọn
        val isBooked = selectedDate?.let { date ->
            bookedAppointments.any { appointment ->
                val bookedDate = appointment.split(" ")[0] // Lấy yyyy-MM-dd
                val bookedTime = appointment.split(" ")[1] + " " + appointment.split(" ")[2] // Lấy h:mm a
                val parsedBookedTime = try {
                    timeFormat.parse(bookedTime)
                } catch (e: Exception) {
                    null
                }
                val normalizedBookedTime = parsedBookedTime?.let { timeFormat.format(it) } ?: bookedTime
                bookedDate == date && normalizedBookedTime == normalizedTime
            }
        } ?: false

        val isSelected = position == selectedPosition

        if (isBooked) {
            // Giờ đã được đặt: tô xám và vô hiệu hóa
            holder.timeText.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.gray))
            holder.timeText.setBackgroundResource(R.drawable.bg_choose_time_booked)
            holder.itemView.isEnabled = false
            holder.itemView.isClickable = false
        } else {
            // Giờ chưa được đặt
            holder.timeText.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    if (isSelected) android.R.color.white else R.color.gray
                )
            )
            holder.timeText.setBackgroundResource(
                if (isSelected) R.drawable.bg_choose_time_selected else R.drawable.bg_choose_time_default
            )
            holder.itemView.isEnabled = true
            holder.itemView.isClickable = true
        }

        holder.itemView.setOnClickListener {
            if (!isBooked) {
                val previous = selectedPosition
                selectedPosition = position
                notifyItemChanged(previous)
                notifyItemChanged(selectedPosition)
                onTimeSelected(time)
            }
        }
    }
}