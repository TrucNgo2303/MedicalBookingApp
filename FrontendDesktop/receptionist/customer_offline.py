import tkinter as tk
from tkinter import messagebox, ttk
import requests
from constants import base_url

# Biến toàn cục
patient_id = None
patient_name = ""
doctor_id_map = {}
specialist_fees = {}

def fetch_specialists():
    try:
        response = requests.get(f"{base_url}/receptionist/all-specialist")
        response.raise_for_status()
        return response.json()
    except Exception as e:
        messagebox.showerror("Lỗi khi lấy danh sách chuyên khoa", str(e))
        return []

def get_specialist_names_and_fees():
    specialists = fetch_specialists()
    return [(s["specialist_name"], int(float(s["consultation_fee"]))) for s in specialists]

def fetch_doctors_by_specialist(specialist_name):
    try:
        res = requests.post(f"{base_url}/receptionist/check-doctor", json={"specialist_name": specialist_name})
        res.raise_for_status()
        data = res.json()
        if data["message"] == "Success":
            return data["data"]
        return []
    except Exception as e:
        messagebox.showerror("Lỗi lấy danh sách bác sĩ", str(e))
        return []

def create_specialist_sidebar(parent):
    container = tk.Frame(parent, bg="#e8f0fe")
    container.pack(side="left", fill="y")

    canvas = tk.Canvas(container, bg="#e8f0fe", highlightthickness=0, width=300)
    scrollbar = ttk.Scrollbar(container, orient="vertical", command=canvas.yview)
    scroll_frame = tk.Frame(canvas, bg="#e8f0fe")

    scroll_frame.bind(
        "<Configure>",
        lambda e: canvas.configure(scrollregion=canvas.bbox("all"))
    )

    canvas.create_window((0, 0), window=scroll_frame, anchor="nw")
    canvas.configure(yscrollcommand=scrollbar.set)

    canvas.pack(side="left", fill="both", expand=True)
    scrollbar.pack(side="right", fill="y")

    specialists = fetch_specialists()
    for specialist in specialists:
        frame = tk.Frame(scroll_frame, bg="white", padx=10, pady=10, relief="solid", bd=1)
        frame.pack(fill="x", padx=5, pady=5)

        tk.Label(frame, text=specialist["specialist_name"], font=("Arial", 12, "bold"), bg="white").pack(anchor="w")
        tk.Label(frame, text=specialist["summary"], wraplength=260, justify="left", font=("Arial", 10), bg="white").pack(anchor="w", pady=4)
        fee = int(float(specialist["consultation_fee"]))
        tk.Label(frame, text=f"Phí khám: {fee:,} VND", font=("Arial", 10, "italic"), bg="white", fg="green").pack(anchor="w")

def run_customer_offline():
    global patient_id, patient_name, doctor_id_map, specialist_fees

    root = tk.Tk()
    root.title("Khách hàng ngoại tuyến")
    root.attributes('-fullscreen', True)

    create_specialist_sidebar(root)

    left_frame = tk.Frame(root, bg="#f7f7f7", padx=20, pady=20)
    left_frame.place(relx=0.3, rely=0.4, anchor="w")

    tk.Label(left_frame, text="Tên bệnh nhân:", font=("Arial", 12)).grid(row=0, column=0, sticky="w")
    name_entry = tk.Entry(left_frame, font=("Arial", 12), width=30)
    name_entry.grid(row=0, column=1, pady=5)

    tk.Label(left_frame, text="Số điện thoại:", font=("Arial", 12)).grid(row=1, column=0, sticky="w")
    phone_entry = tk.Entry(left_frame, font=("Arial", 12), width=30)
    phone_entry.grid(row=1, column=1, pady=5)

    tk.Label(left_frame, text="Địa chỉ:", font=("Arial", 12)).grid(row=2, column=0, sticky="w")
    address_entry = tk.Entry(left_frame, font=("Arial", 12), width=30)
    address_entry.grid(row=2, column=1, pady=5)

    def add_offline_patient():
        name = name_entry.get().strip()
        phone = phone_entry.get().strip()
        address = address_entry.get().strip()
        if not name or not phone or not address:
            messagebox.showwarning("Thiếu thông tin", "Vui lòng nhập đầy đủ họ tên, số điện thoại và địa chỉ.")
            return
        try:
            res = requests.post(f"{base_url}/receptionist/add-offline-patient", json={
                "full_name": name,
                "phone_number": phone,
                "address": address
            })
            result = res.json()
            if result.get("message") == "Success":
                global patient_id, patient_name
                patient_id = result.get("patient_id")
                patient_name = name
                messagebox.showinfo("Thành công", f"Đã thêm người khám (ID: {patient_id})")
                add_btn.config(state="disabled", bg="#BDBDBD")
            elif "error" in result:
                messagebox.showerror("Lỗi", result["error"])
        except Exception as e:
            messagebox.showerror("Lỗi kết nối", str(e))

    add_btn = tk.Button(left_frame, text="Thêm người khám", font=("Arial", 12), bg="#4CAF50", fg="white", command=add_offline_patient)
    add_btn.grid(row=6, column=1, pady=10, sticky="w")

    tk.Label(left_frame, text="Chuyên khoa khám:", font=("Arial", 12)).grid(row=3, column=0, sticky="w")
    specialist_var = tk.StringVar()
    specialist_data = get_specialist_names_and_fees()
    specialist_list = [s[0] for s in specialist_data]
    specialist_fees = {s[0]: s[1] for s in specialist_data}
    if specialist_list:
        specialist_var.set(specialist_list[0])
    specialist_menu = tk.OptionMenu(left_frame, specialist_var, *specialist_list)
    specialist_menu.config(font=("Arial", 12), width=28)
    specialist_menu.grid(row=3, column=1, pady=5, columnspan=2)

    fee_label = tk.Label(left_frame, text="", font=("Arial", 12), fg="green")
    fee_label.grid(row=4, column=1, sticky="w")

    doctor_frame = tk.Frame(left_frame, bg="#ffffff", relief="solid", bd=1)
    doctor_frame.grid(row=5, column=0, columnspan=3, pady=(10, 0), sticky="we")

    selected_doctor = tk.StringVar()

    def update_doctor_list(specialist_name):
        for widget in doctor_frame.winfo_children():
            widget.destroy()
        doctors = fetch_doctors_by_specialist(specialist_name)
        if not doctors:
            tk.Label(doctor_frame, text="Không tìm thấy bác sĩ.", font=("Arial", 11), fg="gray", bg="white").pack()
            return
        doctor_id_map.clear()
        for doc in doctors:
            doctor_id_map[doc["full_name"]] = doc.get("doctor_id")
            count = doc["waiting_count"]
            color = "#4CAF50" if count == 0 else "#FFC107" if count <= 3 else "#F44336"
            text = f"{doc['full_name']} - {count} đang đợi"
            tk.Radiobutton(doctor_frame, text=text, variable=selected_doctor, value=doc["full_name"],
                           font=("Arial", 11), bg="white", anchor="w", indicatoron=True, fg=color).pack(fill="x", padx=10, pady=2)

    def update_fee_and_doctors(*args):
        selected = specialist_var.get()
        fee = specialist_fees.get(selected, 0)
        fee_label.config(text=f"Phí khám: {fee:,} VND")
        update_doctor_list(selected)

    specialist_var.trace_add("write", update_fee_and_doctors)
    update_fee_and_doctors()

    def check_phone():
        phone = phone_entry.get().strip()
        if not phone:
            messagebox.showwarning("Thiếu thông tin", "Vui lòng nhập số điện thoại.")
            return
        try:
            res = requests.post(f"{base_url}/receptionist/check-phone-number", json={"phone_number": phone})
            result = res.json()
            for widget in right_frame.winfo_children():
                widget.destroy()
            if result["message"] == "Phone number is not exist":
                tk.Label(right_frame, text="Người khám này không tồn tại", fg="red", font=("Arial", 12)).pack()
            elif result["message"] == "Phone number is exist":
                data = result["data"]
                global patient_id, patient_name
                patient_id = data["patient_id"]
                patient_name = data["full_name"]
                tk.Label(right_frame, text="Thông tin người khám", font=("Arial", 13, "bold")).pack(pady=(0, 10))
                info = {
                    "Họ tên": data.get("full_name") or "Không có",
                    "SĐT": data.get("phone_number") or "Không có",
                    "Ngày sinh": (data.get("date_of_birth")[:10] if data.get("date_of_birth") else "Không có"),
                    "Giới tính": (
                        "Nữ" if data.get("gender") == "Female"
                        else "Nam" if data.get("gender") == "Male"
                        else "Khác" if data.get("gender") 
                        else "Không có"
                    ),
                    "Địa chỉ": data.get("address") or "Không có"
                }
                for label, value in info.items():
                    tk.Label(right_frame, text=f"{label}: {value}", font=("Arial", 12)).pack(anchor="w")

                def transfer_data():
                    name_entry.delete(0, tk.END)
                    name_entry.insert(0, data["full_name"])
                    phone_entry.delete(0, tk.END)
                    phone_entry.insert(0, data["phone_number"])
                    address_entry.delete(0, tk.END)
                    address_entry.insert(0, data["address"])
                    messagebox.showinfo("Thành công", "Dữ liệu đã được chuyển sang bên trái.")

                tk.Button(right_frame, text="Chuyển sang", font=("Arial", 12), bg="#4CAF50", fg="white",
                          command=transfer_data).pack(pady=10)
        except Exception as e:
            messagebox.showerror("Lỗi", str(e))

    right_frame = tk.Frame(root, bg="#ffffff", padx=20, pady=20, relief="solid", bd=1)
    right_frame.place(relx=0.7, rely=0.4, anchor="w")

    check_btn = tk.Button(left_frame, text="✔", font=("Arial", 12), bg="#2196F3", fg="white", command=check_phone)
    check_btn.grid(row=1, column=2, padx=10)

    def open_schedule_dialog():
        global patient_name
        if not patient_id:
            messagebox.showwarning("Chưa có bệnh nhân", "Vui lòng kiểm tra hoặc thêm bệnh nhân trước.")
            return
        doctor_name = selected_doctor.get()
        if not doctor_name or doctor_name not in doctor_id_map:
            messagebox.showwarning("Chưa chọn bác sĩ", "Vui lòng chọn bác sĩ.")
            return
        doc_id = doctor_id_map[doctor_name]
        fee = specialist_fees.get(specialist_var.get(), 0)
        specialist_name = specialist_var.get()

        dialog = tk.Toplevel()
        dialog.title("Đặt lịch khám")
        dialog.geometry("400x450")
        dialog.attributes("-topmost", True)

        tk.Label(dialog, text=f"Bệnh nhân: {patient_name}", font=("Arial", 11)).pack(pady=3)
        tk.Label(dialog, text=f"Bác sĩ: {doctor_name}", font=("Arial", 11)).pack(pady=3)
        tk.Label(dialog, text=f"Chuyên khoa: {specialist_name}", font=("Arial", 11)).pack(pady=3)
        tk.Label(dialog, text=f"Phí khám: {fee:,} VND", font=("Arial", 11), fg="green").pack(pady=3)

        tk.Label(dialog, text="Tóm tắt bệnh:", font=("Arial", 11)).pack(pady=(10, 3))
        reason_entry = tk.Text(dialog, font=("Arial", 11), height=5, width=40)
        reason_entry.pack()

        appointment_id = None  # Biến cục bộ

        def update_payment():
            confirm = messagebox.askyesno("Xác nhận", "Bạn có chắc chắn đã thanh toán?", parent=dialog)
            if confirm:
                pay_btn.config(state="disabled", bg="#BDBDBD", fg="white")

        pay_btn = tk.Button(dialog, text="Đã thanh toán", font=("Arial", 11), bg="#FF9800", fg="white", command=update_payment)
        pay_btn.pack(pady=10)

        def confirm_schedule():
            nonlocal appointment_id
            reason = reason_entry.get("1.0", "end").strip()
            is_paid = pay_btn["state"] == "disabled"
            try:
                res = requests.post(f"{base_url}/receptionist/add-customer-offline", json={
                    "patient_id": patient_id,
                    "doctor_id": doc_id,
                    "reason": reason if reason else None,
                    "consultation_fee": fee,
                    "is_paid": is_paid
                })
                result = res.json()
                if result.get("message") == "Success":
                    appointment_id = result.get("appointment_id")
                    messagebox.showinfo("Thành công", f"Đặt lịch thành công. Mã lịch hẹn: {appointment_id}", parent=dialog)
                    dialog.destroy()
                else:
                    messagebox.showerror("Lỗi", "Không thể đặt lịch.", parent=dialog)
            except Exception as e:
                messagebox.showerror("Lỗi kết nối", str(e), parent=dialog)

        tk.Button(dialog, text="Xác nhận đặt lịch", font=("Arial", 11), bg="#4CAF50", fg="white", command=confirm_schedule).pack(pady=10)

    schedule_btn = tk.Button(left_frame, text="Tạo lịch đặt", font=("Arial", 12), bg="#2196F3", fg="white", command=open_schedule_dialog)
    schedule_btn.grid(row=7, column=1, pady=10, sticky="w")

    # Nút X và -
    control_frame = tk.Frame(root, bg="#e0e0e0")
    control_frame.place(relx=1.0, x=-10, y=10, anchor="ne")
    tk.Button(control_frame, text="-", fg="black", bg="white", font=("Arial", 12, "bold"),
              command=root.iconify, bd=1, relief="solid", padx=10, pady=2).pack(side="left", padx=(0, 5))
    tk.Button(control_frame, text="X", fg="white", bg="red", font=("Arial", 12, "bold"),
              command=root.destroy, bd=1, relief="solid", padx=10, pady=2).pack(side="left")

    def back(current_window):
        from receptionist.receptionist_home import run_receptionist_home
        current_window.destroy()
        run_receptionist_home()

    tk.Button(root, text="Quay lại", font=("Arial", 14), bg="#9E9E9E", fg="white",
              command=lambda: back(root)).place(relx=1.0, rely=1.0, x=-20, y=-20, anchor="se")

    root.mainloop()

if __name__ == "__main__":
    run_customer_offline()
