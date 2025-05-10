import tkinter as tk

def run_admin_home():
    root = tk.Tk()
    root.title("Admin Home")

    # Giao diện toàn màn hình
    root.attributes('-fullscreen', True)

    # Tiêu đề
    tk.Label(root, text="Chào mừng Admin!", font=("Arial", 32, "bold")).pack(pady=50)

    # Frame chứa 2 nút ở giữa
    center_frame = tk.Frame(root)
    center_frame.pack(expand=True)

    # Nút Khách hàng trực tuyến
    btn_online = tk.Button(center_frame, text="Khách hàng trực tuyến", font=("Arial", 20, "bold"),
                           width=25, height=3, bg="#2196F3", fg="white")
    btn_online.pack(pady=20)

    # Nút Khách hàng trực tiếp
    btn_offline = tk.Button(center_frame, text="Khách hàng trực tiếp", font=("Arial", 20, "bold"),
                            width=25, height=3, bg="#4CAF50", fg="white")
    btn_offline.pack(pady=20)

    # Nút X và - ở góc trên phải
    control_frame = tk.Frame(root, bg="#e0e0e0")
    control_frame.place(relx=1.0, x=-10, y=10, anchor="ne")

    btn_minimize = tk.Button(control_frame, text="-", fg="black", bg="white",
                             font=("Arial", 12, "bold"),
                             command=root.iconify, bd=1, relief="solid", padx=10, pady=2)
    btn_minimize.pack(side="left", padx=(0, 5))

    btn_exit = tk.Button(control_frame, text="X", fg="white", bg="red",
                         font=("Arial", 12, "bold"),
                         command=root.destroy, bd=1, relief="solid", padx=10, pady=2)
    btn_exit.pack(side="left")

    root.mainloop()
