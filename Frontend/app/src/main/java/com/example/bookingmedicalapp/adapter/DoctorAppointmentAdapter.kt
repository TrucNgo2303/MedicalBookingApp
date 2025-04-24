package com.example.bookingmedicalapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.model.DoctorAppointment
import de.hdodenhof.circleimageview.CircleImageView

class DoctorAppointmentAdapter(
    private val appointments: MutableList<DoctorAppointment> = mutableListOf()
) : RecyclerView.Adapter<DoctorAppointmentAdapter.AppointmentViewHolder>() {

    // Khai báo callback cho sự kiện click, nhận appointment_id
    var onItemClick: ((Int) -> Unit)? = null

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val civAvatar: CircleImageView = itemView.findViewById(R.id.civ_avatar)
        private val tvPatientName: AppCompatTextView = itemView.findViewById(R.id.tv_patient_name)
        private val tvStatus: AppCompatTextView = itemView.findViewById(R.id.tv_status)
        private val tvDate: AppCompatTextView = itemView.findViewById(R.id.tv_date)
        private val tvTime: AppCompatTextView = itemView.findViewById(R.id.tv_time)
        private val tvIsOnline: AppCompatTextView = itemView.findViewById(R.id.tv_is_online)

        fun bind(appointment: DoctorAppointment) {
            tvPatientName.text = appointment.patient_name
            when (appointment.status) {
                "Pending" -> {
                    tvStatus.text = "Chờ xác nhận"
                    tvStatus.setTextColor(Color.YELLOW)
                }
                "Confirmed" -> {
                    tvStatus.text = "Sắp tới"
                    tvStatus.setTextColor(Color.BLUE)
                }
                "Completed" -> {
                    tvStatus.text = "Hoàn thành"
                    tvStatus.setTextColor(Color.GREEN)
                }
                "Cancelled" -> {
                    tvStatus.text = "Đã hủy"
                    tvStatus.setTextColor(Color.RED)
                }
                else -> {
                    tvStatus.text = appointment.status
                    tvStatus.setTextColor(Color.BLACK)
                }
            }
            tvDate.text = appointment.date
            tvTime.text = appointment.time

            if (appointment.avatar.isEmpty()) {
                civAvatar.setImageResource(R.drawable.default_avatar)
            } else {
                Glide.with(civAvatar.context)
                    .load(appointment.avatar)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(civAvatar)
            }
            when( appointment.is_online){
                1 -> {
                    tvIsOnline.text = "Trực tuyến"
                    tvIsOnline.setTextColor(Color.GREEN)
                }
                0 -> {
                    tvIsOnline.text = "Trực tiếp"
                    tvIsOnline.setTextColor(Color.RED)
                }
                else -> {
                    tvIsOnline.text = "Không xác định"
                    tvIsOnline.setTextColor(Color.BLACK)
                }
            }

            // Sửa lỗi invoke: Kiểm tra null và truyền appointment_id
            itemView.setOnClickListener {
                onItemClick?.invoke(appointment.appointment_id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor_appoitment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(appointments[position])
    }

    override fun getItemCount(): Int = appointments.size

    fun updateAppointments(newAppointments: List<DoctorAppointment>) {
        appointments.clear()
        appointments.addAll(newAppointments)
        notifyDataSetChanged()
    }
}