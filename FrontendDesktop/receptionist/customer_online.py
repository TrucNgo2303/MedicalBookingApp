import tkinter as tk
import requests
from constants import base_url
from tkinter import messagebox
from datetime import datetime

def run_customer_online():
    root = tk.Tk()
    root.title("Khách hàng trực tuyến")
    root.attributes('-fullscreen', True)

    appointments_data = []
    search_var = tk.StringVar()
    per_row = 4  # số frame mỗi hàng

    def filter_appointments():
        search_text = search_entry.get().strip()  # dùng trực tiếp Entry thay vì StringVar
        for widget in scroll_frame.winfo_children():
            widget.destroy()

        # Chỉ giữ lại những SĐT bắt đầu bằng chuỗi nhập
        if search_text == "":
            filtered = appointments_data
        else:
            filtered = [
                app for app in appointments_data
                if app["patient_phone_number"].startswith(search_text)
            ]

        if not filtered:
            tk.Label(scroll_frame, text="Không tìm thấy lịch hẹn nào với số điện thoại này.",
                     font=("Arial", 14), fg="gray").grid(row=0, column=0, padx=20, pady=20)
            return

        col = 0
        row = 0
        for app in filtered:
            render_appointment(app, row, col, highlight=True)
            col += 1
            if col >= per_row:
                col = 0
                row += 1

    def render_appointment(app, row, col, highlight=False):
        border_color = "#2196F3" if highlight else "black"

        frame = tk.Frame(scroll_frame, bd=2, relief="solid", padx=10, pady=10,
                         bg="#f0f8ff", width=300, height=220, highlightthickness=2)
        frame.grid(row=row, column=col, padx=10, pady=15)
        frame.grid_propagate(False)
        frame.config(highlightbackground=border_color)

        dt = datetime.strptime(app["appointment_datetime"], "%Y-%m-%d %H:%M:%S")
        date_str = dt.strftime("%d/%m/%Y")
        time_str = dt.strftime("%H:%M")

        status_map = {
            "Confirmed": ("Xác nhận", "#2196F3"),
            "Complete": ("Hoàn thành", "#4CAF50"),
            "Cancelled": ("Đã hủy", "#F44336")
        }
        status_text, status_color = status_map.get(app["status"], (app["status"], "black"))

        info = (
            f"Mã hẹn: {app['appointment_id']}\n"
            f"Ngày: {date_str} - Giờ: {time_str}\n"
            f"Bệnh nhân: {app['patient_name']}\n"
            f"SĐT: {app['patient_phone_number']}\n"
            f"Bác sĩ: {app['doctor_name']} ({app['doctor_specialty']})\n"
            f"Lý do: {app['reason']}\n"
            f"Phí khám: {int(float(app['consultation_fee'])):,} VNĐ\n"
            f"Đặt cọc: {'Đã đặt cọc' if app['is_deposit'] else 'Chưa đặt cọc'}"
        )

        tk.Label(frame, text=info, justify="left", font=("Arial", 11), bg="#f0f8ff").pack(anchor="w")
        tk.Label(frame, text=status_text, font=("Arial", 11, "bold"),
                 fg="white", bg=status_color, padx=10, pady=2).pack(anchor="w", pady=(5, 0))

        # Bắt sự kiện click để lưu appointment_id và chuyển màn hình
        def on_click(event):
            import session
            appointment_id_clicked = app["appointment_id"]
            session.appointment_id = appointment_id_clicked
            print(f"[DEBUG] appointment_id: {session.appointment_id}")

            def open_detail():
                from receptionist import appointment_detail
                print(f"[DEBUG] appointment_id (trước run_detail): {session.appointment_id}")
                appointment_detail.run_detail()

            # Delay nhỏ đảm bảo session cập nhật xong
            root.after(200, open_detail)
            root.after(300, root.destroy)  # đóng sau khi mở chi tiết


        frame.bind("<Button-1>", on_click)
        for child in frame.winfo_children():
            child.bind("<Button-1>", on_click)


    # Nút X và -
    control_frame = tk.Frame(root, bg="#e0e0e0")
    control_frame.place(relx=1.0, x=-10, y=10, anchor="ne")

    tk.Button(control_frame, text="-", fg="black", bg="white", font=("Arial", 12, "bold"),
              command=root.iconify, bd=1, relief="solid", padx=10, pady=2).pack(side="left", padx=(0, 5))
    tk.Button(control_frame, text="X", fg="white", bg="red", font=("Arial", 12, "bold"),
              command=root.destroy, bd=1, relief="solid", padx=10, pady=2).pack(side="left")

    # Tìm kiếm
    search_frame = tk.Frame(root)
    search_frame.pack(pady=20)
    tk.Label(search_frame, text="Tìm theo SĐT:", font=("Arial", 14)).pack(side="left", padx=5)
    search_entry = tk.Entry(search_frame, textvariable=search_var, font=("Arial", 14), width=30)
    search_entry.pack(side="left", padx=5)
    tk.Button(search_frame, text="Tìm", font=("Arial", 12, "bold"),
              bg="#2196F3", fg="white", command=filter_appointments).pack(side="left", padx=5)

    # Scroll canvas
    canvas = tk.Canvas(root)
    canvas.pack(side="left", fill="both", expand=True)
    scrollbar = tk.Scrollbar(root, orient="vertical", command=canvas.yview)
    scrollbar.pack(side="right", fill="y")
    canvas.configure(yscrollcommand=scrollbar.set)

    scroll_frame = tk.Frame(canvas)
    canvas.create_window((0, 0), window=scroll_frame, anchor="nw")

    def on_mousewheel(event):
        canvas.yview_scroll(int(-1 * (event.delta / 120)), "units")
    canvas.bind_all("<MouseWheel>", on_mousewheel)

    def resize_canvas(event):
        canvas.configure(scrollregion=canvas.bbox("all"))
    scroll_frame.bind("<Configure>", resize_canvas)

    def back(current_window):
        from receptionist.receptionist_home import run_receptionist_home
        current_window.destroy()
        run_receptionist_home()
    tk.Button(root, text="Quay lại", font=("Arial", 14), bg="#9E9E9E", fg="white",
          command=lambda: back(root)).place(relx=1.0, rely=1.0, x=-20, y=-20, anchor="se")

    # Gọi API
    try:
        response = requests.get(f"{base_url}/receptionist/get-online-appointments")
        response.raise_for_status()
        appointments_data = response.json()
        filter_appointments()
    except requests.exceptions.RequestException as e:
        messagebox.showerror("Lỗi", f"Không thể tải dữ liệu:\n{e}")

    root.mainloop()

if __name__ == "__main__":
    run_customer_online()
