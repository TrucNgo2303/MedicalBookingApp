# appointment_detail.py
import tkinter as tk
import constants

def run_detail():
    root = tk.Tk()
    root.title("Chi tiết lịch hẹn")
    root.geometry("600x400")

    appt_id = constants.appointment_id
    tk.Label(root, text=f"Chi tiết lịch hẹn - ID: {appt_id}", font=("Arial", 16)).pack(pady=20)

    # Thêm nội dung chi tiết tùy bạn xử lý API hoặc hiển thị đơn giản
    # ...

    tk.Button(root, text="Đóng", command=root.destroy).pack(pady=20)
    root.mainloop()

if __name__ == "__main__":
    run_detail()
