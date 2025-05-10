import tkinter as tk
from tkinter import messagebox
import requests
from constants import base_url

class LoginApp:
    def __init__(self, root):
        self.root = root
        self.root.title("Login Screen")
        self.root.attributes('-fullscreen', True)
        self.root.configure(bg="#e0e0e0")

        # Nút X và - trên góc
        control_frame = tk.Frame(root, bg="#e0e0e0")
        control_frame.place(relx=1.0, x=-10, y=10, anchor="ne")

        btn_minimize = tk.Button(control_frame, text="-", fg="black", bg="white", font=("Arial", 12, "bold"),
                                command=lambda: self.root.iconify(), bd=1, relief="solid", padx=10, pady=2)
        btn_minimize.pack(side="left", padx=(0, 5))

        btn_exit = tk.Button(control_frame, text="X", fg="white", bg="red", font=("Arial", 12, "bold"),
                            command=self.root.destroy, bd=1, relief="solid", padx=10, pady=2)
        btn_exit.pack(side="left")

        # Khung login
        self.login_frame = tk.Frame(self.root, bg="white", bd=2, relief="groove")
        self.login_frame.place(relx=0.5, rely=0.5, anchor="center", width=420, height=280)

        tk.Label(self.login_frame, text="Đăng nhập", font=("Arial", 18, "bold"), bg="white").pack(pady=10)

        form_frame = tk.Frame(self.login_frame, bg="white")
        form_frame.pack(pady=10)

        # Email row
        tk.Label(form_frame, text="Email:", bg="white", width=12, anchor="e", font=("Arial", 12)).grid(row=0, column=0, padx=10, pady=5)
        self.email_entry = tk.Entry(form_frame, width=30, font=("Arial", 12))
        self.email_entry.grid(row=0, column=1, padx=10, pady=5)

        # Mật khẩu row (Entry + nút 👁)
        tk.Label(form_frame, text="Mật khẩu:", bg="white", width=12, anchor="e", font=("Arial", 12)).grid(row=1, column=0, padx=10, pady=5)

        pw_frame = tk.Frame(form_frame, bg="white")
        pw_frame.grid(row=1, column=1, padx=10, pady=5, sticky="w")

        self.password_entry = tk.Entry(pw_frame, width=25, font=("Arial", 12), show="*")
        self.password_entry.pack(side="left")

        self.show_password = False

        def toggle_password():
            self.show_password = not self.show_password
            if self.show_password:
                self.password_entry.config(show="")
                eye_button.config(text="👁‍🗨")  # đang hiện
            else:
                self.password_entry.config(show="*")
                eye_button.config(text="👁")  # đang ẩn

        eye_button = tk.Button(pw_frame, text="👁", command=toggle_password,
                            relief="flat", bg="white", font=("Arial", 12))
        eye_button.pack(side="left", padx=(5, 0))

        # Nút đăng nhập
        tk.Button(self.login_frame, text="Đăng nhập", width=20, bg="#4CAF50", fg="white",
                font=("Arial", 12), command=self.login).pack(pady=20)

        self.root.bind("<Escape>", lambda e: self.root.destroy())

    def login(self):
        email = self.email_entry.get()
        password = self.password_entry.get()

        if not email or not password:
            messagebox.showwarning("Thiếu thông tin", "Vui lòng nhập đầy đủ email và mật khẩu.")
            return

        try:
            response = requests.post(f"{base_url}/auth/login", json={
                "email": email,
                "password": password
            })

            if response.status_code == 200:
                res_json = response.json()
                if res_json.get("message") == "Success":
                    role = res_json.get("role")
                    if role == "Admin":
                        import admin.admin_home as admin_home
                        self.root.destroy()
                        admin_home.run_admin_home()
                    elif role == "Receptionist":
                        import receptionist.receptionist_home as receptionist_home
                        self.root.destroy()
                        receptionist_home.run_receptionist_home()
                    else:
                        messagebox.showerror("Lỗi", f"Vai trò không được hỗ trợ: {role}")
                else:
                    messagebox.showerror("Lỗi", res_json.get("message", "Lỗi không xác định"))
            else:
                messagebox.showerror("Lỗi", f"Đăng nhập thất bại: {response.json().get('message', 'Lỗi không xác định')}")
        except Exception as e:
            messagebox.showerror("Lỗi kết nối", f"Không thể kết nối tới server.\n{e}")


if __name__ == "__main__":
    root = tk.Tk()
    app = LoginApp(root)
    root.mainloop()
