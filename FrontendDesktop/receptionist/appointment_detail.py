import tkinter as tk
import requests
from datetime import datetime
from tkinter import messagebox
from constants import base_url
import session

def run_detail():
    root = tk.Tk()
    root.title("Chi tiết lịch hẹn")
    root.attributes('-fullscreen', True)

    print(f"[DEBUG] appointment_id in appointment_detail : {session.appointment_id}")

    # Gọi API lấy chi tiết lịch hẹn
    try:
        response = requests.post(f"{base_url}/receptionist/get-online-appointments-detail", json={"appointment_id": session.appointment_id})
        app = response.json()
    except Exception as e:
        app = {"error": str(e)}

    # Frame hiển thị nội dung ở giữa màn hình
    detail_frame = tk.Frame(root, bg="white", padx=40, pady=30, relief="solid", bd=2)
    detail_frame.place(relx=0.5, rely=0.5, anchor="center")

    if "error" in app:
        tk.Label(detail_frame, text=f"Lỗi: {app['error']}", font=("Arial", 14), fg="red").pack()
    else:
        # Format thời gian
        dt = datetime.strptime(app["appointment_datetime"], "%Y-%m-%d %H:%M:%S")
        date_str = dt.strftime("%d/%m/%Y")
        time_str = dt.strftime("%H:%M")

        # Map trạng thái
        status_map = {
            "Confirmed": ("Xác nhận", "#2196F3"),
            "Complete": ("Hoàn thành", "#4CAF50"),
            "Cancelled": ("Đã hủy", "#F44336")
        }
        status_text, status_color = status_map.get(app["status"], (app["status"], "black"))

        # Hiển thị thông tin bằng grid
        labels = [
            ("Mã lịch hẹn:", app["appointment_id"]),
            ("Tên bệnh nhân:", app["patient_name"]),
            ("SĐT bệnh nhân:", app["patient_phone_number"]),
            ("Bác sĩ:", f"{app['doctor_name']} ({app['doctor_specialty']})"),
            ("Ngày khám:", date_str),
            ("Giờ khám:", time_str),
            ("Lý do khám:", app["reason"]),
            ("Phí khám:", f"{int(float(app['consultation_fee'])):,} VND"),
            ("Trạng thái:", status_text),
            ("Đặt cọc:", "Đã đặt cọc" if app["is_deposit"] else "Chưa đặt cọc"),
            ("Thanh toán:", "Đã thanh toán" if app["is_paid"] else "Chưa thanh toán")
        ]

        for i, (label, value) in enumerate(labels):
            tk.Label(detail_frame, text=label, font=("Arial", 13, "bold"), anchor="w").grid(row=i, column=0, sticky="w", padx=(0, 20), pady=5)
            tk.Label(detail_frame, text=value, font=("Arial", 13),
                     fg=(status_color if "Trạng thái" in label else "black"),
                     anchor="w").grid(row=i, column=1, sticky="w", pady=5)

        # Nút xác nhận đã thanh toán
        def open_confirm_dialog():
            confirm_win = tk.Toplevel(root)
            confirm_win.title("Xác nhận")
            confirm_win.geometry("350x150")
            confirm_win.transient(root)
            confirm_win.grab_set()
            confirm_win.resizable(False, False)

            # Hiển thị giữa màn hình
            confirm_win.update_idletasks()
            w, h = 350, 150
            x = (root.winfo_screenwidth() // 2) - (w // 2)
            y = (root.winfo_screenheight() // 2) - (h // 2)
            confirm_win.geometry(f"{w}x{h}+{x}+{y}")

            tk.Label(confirm_win, text="Bạn có chắc muốn xác nhận đã thanh toán?", font=("Arial", 12)).pack(pady=20)
            btn_frame = tk.Frame(confirm_win)
            btn_frame.pack()

            def on_yes():
                try:
                    res = requests.post(f"{base_url}/receptionist/payment-status-update", json={"appointment_id": appointment_id})
                    result = res.json()
                    if result.get("message") == "Success":
                        confirm_win.destroy()
                        root.destroy()
                        run_detail()
                    else:
                        messagebox.showerror("Lỗi", result.get("message"))
                except Exception as ex:
                    messagebox.showerror("Lỗi", str(ex))

            tk.Button(btn_frame, text="Đồng ý", font=("Arial", 12), bg="#2196F3", fg="white", width=10, command=on_yes).pack(side="left", padx=10)
            tk.Button(btn_frame, text="Hủy", font=("Arial", 12), bg="gray", fg="white", width=10, command=confirm_win.destroy).pack(side="left", padx=10)

        is_paid = app.get("is_paid", 0)
        btn_text = "Xác nhận đã thanh toán"
        btn_state = "disabled" if is_paid else "normal"
        btn_bg = "#BDBDBD" if is_paid else "#2196F3"

        tk.Button(detail_frame, text=btn_text, state=btn_state, font=("Arial", 13),
                  bg=btn_bg, fg="white", command=open_confirm_dialog, width=30).grid(columnspan=2, pady=(20, 0))

        # Nút "Thêm vào hàng đợi"
        def add_to_waiting_list():
            try:
                payload = {
                    "appointment_id": app["appointment_id"],
                    "patient_id": app["patient_id"],
                    "doctor_id": app["doctor_id"],
                    "priority": 1
                }
                res = requests.post(f"{base_url}/receptionist/add-to-waiting-list", json=payload)
                result = res.json()
                if result.get("message") == "Success":
                    messagebox.showinfo("Thành công", "Đã thêm vào hàng đợi thành công.")
                    add_queue_btn.config(state="disabled", bg="#BDBDBD")
                    # Không gọi root.destroy() ở đây
                else:
                    messagebox.showerror("Thất bại", result.get("message", "Người khám này đã có trong hàng đợi."))
            except Exception as ex:
                messagebox.showerror("Lỗi", str(ex))

        add_queue_btn = tk.Button(root, text="Thêm vào hàng đợi", font=("Arial", 14), bg="#4CAF50", fg="white",
                                  command=add_to_waiting_list)
        add_queue_btn.place(relx=0.5, rely=1.0, y=-30, anchor="s")

    # Nút X và -
    control_frame = tk.Frame(root, bg="#e0e0e0")
    control_frame.place(relx=1.0, x=-10, y=10, anchor="ne")

    tk.Button(control_frame, text="-", fg="black", bg="white", font=("Arial", 12, "bold"),
              command=root.iconify, bd=1, relief="solid", padx=10, pady=2).pack(side="left", padx=(0, 5))
    tk.Button(control_frame, text="X", fg="white", bg="red", font=("Arial", 12, "bold"),
              command=root.destroy, bd=1, relief="solid", padx=10, pady=2).pack(side="left")

    def back(current_window):
        from receptionist.customer_online import run_customer_online
        current_window.destroy()
        run_customer_online()
    tk.Button(root, text="Quay lại", font=("Arial", 14), bg="#9E9E9E", fg="white",
          command=lambda: back(root)).place(relx=1.0, rely=1.0, x=-20, y=-20, anchor="se")

    root.mainloop()

if __name__ == "__main__":
    run_detail()
