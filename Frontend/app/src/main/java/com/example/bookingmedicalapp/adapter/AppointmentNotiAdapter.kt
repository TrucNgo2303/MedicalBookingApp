package com.example.bookingmedicalapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.model.AppointmentNotiItem
import java.text.DecimalFormat

class AppointmentNotiAdapter(
    private val items: List<AppointmentNotiItem>,
    private val message: String,
    private val messageColor: Int,
    private val onItemClick: (AppointmentNotiItem) -> Unit
) : RecyclerView.Adapter<AppointmentNotiAdapter.AppointmentNotiViewHolder>() {

    inner class AppointmentNotiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: AppCompatTextView = view.findViewById(R.id.tv_date)
        val time: AppCompatTextView = view.findViewById(R.id.tv_time)
        val avatar: AppCompatImageView = view.findViewById(R.id.imv_avatar)
        val name: AppCompatTextView = view.findViewById(R.id.tv_doctor_name)
        val specialist: AppCompatTextView = view.findViewById(R.id.tv_specialist)
        val fee: AppCompatTextView = view.findViewById(R.id.tv_fee)
        val message: AppCompatTextView = view.findViewById(R.id.tv_message)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position])
                }
            }
        }
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

        // Load ảnh avatar với Glide (ảnh tròn + ảnh mặc định khi lỗi)
        Glide.with(holder.itemView.context)
            .load(item.avatar)
            .placeholder(R.drawable.default_avatar)
            .error(R.drawable.default_avatar)
            .transform(CircleCrop())
            .into(holder.avatar)

        holder.name.text = item.name
        holder.specialist.text = item.specialist
        holder.fee.text = formatFee(item.fee)
        holder.message.text = message
        holder.message.setTextColor(ContextCompat.getColor(holder.itemView.context, messageColor))
    }

    private fun formatFee(fee: String): String {
        // Chuyển fee từ String sang Double
        val feeDouble = fee.toDoubleOrNull()

        return if (feeDouble != null) {
            val decimalFormat = DecimalFormat("#,###.##")
            decimalFormat.format(feeDouble)
        } else {
            fee
        }
    }
}
