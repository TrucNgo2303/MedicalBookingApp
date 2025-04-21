package com.example.bookingmedicalapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.model.AllSpecialistResponse
import de.hdodenhof.circleimageview.CircleImageView

class SpecialistAdapter(
    private val specialistList: List<AllSpecialistResponse>,
    private val onItemClick: (AllSpecialistResponse) -> Unit
) : RecyclerView.Adapter<SpecialistAdapter.SpecialistViewHolder>() {

    inner class SpecialistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val tvNumberOfDoctor: TextView = itemView.findViewById(R.id.tv_number_of_doctor)
        private val tvFee: TextView = itemView.findViewById(R.id.tv_fee)
        private val civAvatar: CircleImageView = itemView.findViewById(R.id.civ_avatar)

        // Bắt sự kiện click vào item
        fun bind(specialist: AllSpecialistResponse) {
            tvName.text = specialist.specialist_name
            tvNumberOfDoctor.text = ": ${specialist.numberOfDoctor}"
            tvFee.text = specialist.consultation_fee

            // Load ảnh từ URL bằng Glide với ảnh mặc định nếu lỗi
            Glide.with(itemView.context)
                .load(specialist.icon)
                .placeholder(R.drawable.default_avatar) // Ảnh mặc định khi đang tải
                .error(R.drawable.default_avatar) // Ảnh mặc định nếu lỗi
                .into(civAvatar)

            // Xử lý click item
            itemView.setOnClickListener {
                onItemClick(specialist)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_all_specialist, parent, false)
        return SpecialistViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpecialistViewHolder, position: Int) {
        val specialist = specialistList[position]
        holder.bind(specialist)
    }

    override fun getItemCount(): Int = specialistList.size
}
