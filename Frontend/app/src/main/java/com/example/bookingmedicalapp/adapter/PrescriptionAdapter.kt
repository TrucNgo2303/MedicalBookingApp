package com.example.bookingmedicalapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.model.PrescriptionDetails

class PrescriptionAdapter(
    private val onChangeClick: (PrescriptionDetails) -> Unit,
    private val onDeleteClick: (PrescriptionDetails) -> Unit
) : ListAdapter<PrescriptionDetails, PrescriptionAdapter.PrescriptionViewHolder>(PrescriptionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrescriptionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_prescription, parent, false)
        return PrescriptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrescriptionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PrescriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val edtMedicine: EditText = itemView.findViewById(R.id.edt_medicine)
        private val edtDosage: EditText = itemView.findViewById(R.id.edt_dosage)
        private val edtUsage: EditText = itemView.findViewById(R.id.edt_usage)
        private val edtDuration: EditText = itemView.findViewById(R.id.edt_duration)
        private val btnChange: AppCompatButton = itemView.findViewById(R.id.btn_change)
        private val btnDelete: AppCompatButton = itemView.findViewById(R.id.btn_delete)

        fun bind(prescription: PrescriptionDetails) {
            // Gán dữ liệu vào các EditText
            edtMedicine.setText(prescription.medicine_name)
            edtDosage.setText(prescription.dosage)
            edtUsage.setText(prescription.usage_instruction)
            edtDuration.setText(prescription.duration.toString())

            // Xử lý sự kiện click nút "Thay đổi"
            btnChange.setOnClickListener {
                val updatedPrescription = prescription.copy(
                    medicine_name = edtMedicine.text.toString(),
                    dosage = edtDosage.text.toString(),
                    usage_instruction = edtUsage.text.toString(),
                    duration = edtDuration.text.toString().toIntOrNull() ?: prescription.duration
                )
                onChangeClick(updatedPrescription)
            }

            // Xử lý sự kiện click nút "Xóa"
            btnDelete.setOnClickListener {
                onDeleteClick(prescription)
            }
        }
    }

    class PrescriptionDiffCallback : DiffUtil.ItemCallback<PrescriptionDetails>() {
        override fun areItemsTheSame(oldItem: PrescriptionDetails, newItem: PrescriptionDetails): Boolean {
            return oldItem.prescription_id == newItem.prescription_id
        }

        override fun areContentsTheSame(oldItem: PrescriptionDetails, newItem: PrescriptionDetails): Boolean {
            return oldItem == newItem
        }
    }
}