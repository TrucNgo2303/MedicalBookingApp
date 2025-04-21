package com.example.bookingmedicalapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.model.SpecialistDetailResponse
import de.hdodenhof.circleimageview.CircleImageView

class SpecialistDetailAdapter(
    private val doctorList: List<SpecialistDetailResponse>,
    private val onItemClick: (SpecialistDetailResponse) -> Unit
) : RecyclerView.Adapter<SpecialistDetailAdapter.SpecialistDetailViewHolder>() {

    class SpecialistDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val civAvatar: CircleImageView = itemView.findViewById(R.id.civ_avatar)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvSpecialty: TextView = itemView.findViewById(R.id.tv_specialist_doctor)
        val tvQualification: TextView = itemView.findViewById(R.id.tv_qualification)
        val tvRating: TextView = itemView.findViewById(R.id.tv_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialistDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor_specialist, parent, false)
        return SpecialistDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpecialistDetailViewHolder, position: Int) {
        val doctor = doctorList[position]

        // Gán dữ liệu từ API vào item
        holder.tvName.text = doctor.full_name ?: "N/A"
        holder.tvSpecialty.text = doctor.specialty ?: "Chưa cập nhật"
        holder.tvQualification.text = doctor.qualification ?: "Chưa cập nhật"
        holder.tvRating.text = doctor.average_star ?: "0.0"

        // Load ảnh bằng Glide
        Glide.with(holder.itemView.context)
            .load(doctor.avatar)
            .placeholder(R.drawable.default_avatar) // Ảnh mặc định khi đang tải
            .error(R.drawable.default_avatar) // Ảnh mặc định nếu lỗi
            .into(holder.civAvatar)

        // Bắt sự kiện click item
        holder.itemView.setOnClickListener {
            onItemClick(doctor)
        }
    }

    override fun getItemCount(): Int = doctorList.size
}
