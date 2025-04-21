package com.example.bookingmedicalapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.model.AppointmentNotiItem

class AppointmentNotiAdapter(
    private val items: List<AppointmentNotiItem>,
    private val message: String,
    private val isEnabled: Boolean // Thêm biến boolean
) : RecyclerView.Adapter<AppointmentNotiAdapter.AppointmentNotiViewHolder>() {

    inner class AppointmentNotiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: AppCompatTextView = view.findViewById(R.id.tv_date)
        val time: AppCompatTextView = view.findViewById(R.id.tv_time)
        val avatar: AppCompatImageView = view.findViewById(R.id.imv_avatar)
        val name: AppCompatTextView = view.findViewById(R.id.tv_doctor_name)
        val specialist: AppCompatTextView = view.findViewById(R.id.tv_specialist)
        val fee: AppCompatTextView = view.findViewById(R.id.tv_fee)
        val message: AppCompatTextView = view.findViewById(R.id.tv_message)
        val btnCancel: AppCompatButton = view.findViewById(R.id.btn_cancel)
        val btnReschedule: AppCompatButton = view.findViewById(R.id.btn_reschedule)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentNotiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appoiment_noti, parent, false)
        return AppointmentNotiViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: AppointmentNotiViewHolder, position: Int) {
        val item = items[position]
        holder.date.text = item.date
        holder.time.text = item.time
        holder.avatar.setImageResource(item.avatar)
        holder.name.text = item.name
        holder.specialist.text = item.specialist
        holder.fee.text = item.fee
        holder.message.text = message

        if (!isEnabled) {
            holder.btnCancel.visibility = View.GONE

            val layoutParams = holder.btnReschedule.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.marginStart = 8.dpToPx(holder.itemView.context)
            layoutParams.marginEnd = 8.dpToPx(holder.itemView.context)
            holder.btnReschedule.layoutParams = layoutParams
        }
    }
    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

}
