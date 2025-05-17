DROP DATABASE MedicalBooking;
CREATE DATABASE MedicalBooking;

-- Sử dụng cơ sở dữ liệu
USE MedicalBooking;

CREATE TABLE Patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15),
    date_of_birth DATE,
    gender ENUM('Male', 'Female', 'Other'),
    address TEXT,
    authorization_id INT,
    avatar TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Doctors (
    doctor_id INT PRIMARY KEY AUTO_INCREMENT ,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15),
    specialty VARCHAR(100) NOT NULL,
    qualification VARCHAR(100),
    experience_years INT DEFAULT 0,
    summary TEXT,
    specialist_id INT,
    authorization_id INT,
    avatar TEXT,
    patient_count INT NOT NULL DEFAULT 0,
    status ENUM('Inative', 'Active') DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Appointments (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_datetime DATETIME NOT NULL,
    status ENUM('Pending', 'Confirmed', 'Completed', 'Cancelled') DEFAULT 'Pending',
    reason TEXT,
    is_deposit BOOLEAN DEFAULT TRUE,
    consultation_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    is_paid  BOOLEAN DEFAULT FALSE,
    is_online BOOLEAN DEFAULT TRUE,
    payment_status VARCHAR(255), 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE Notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    authorization_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Specialists (
    specialist_id INT PRIMARY KEY AUTO_INCREMENT,
    specialist_name VARCHAR(255) NOT NULL,
    summary TEXT,
    icon VARCHAR(255),
	consultation_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00
);

CREATE TABLE Authorizations (
    authorization_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('Patient', 'Doctor', 'Admin', 'Receptionist') NOT NULL
);

CREATE TABLE Comments (
    comment_id INT PRIMARY KEY AUTO_INCREMENT,
    doctor_id INT NOT NULL,
    patient_id INT NOT NULL,
    comment_detail TEXT,
    star INT CHECK (star BETWEEN 1 AND 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE CommentReplies (
    reply_id INT AUTO_INCREMENT PRIMARY KEY,
    comment_id INT NOT NULL,
    user_type ENUM('Doctor', 'Admin', "Patient") NOT NULL,
    user_id INT NOT NULL,
    reply_detail TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE Waiting_list (
	waiting_list_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    priority INT NOT NULL DEFAULT 0,
    appointment_id INT DEFAULT NULL,
    status ENUM ('waiting','assigned') DEFAULT 'waiting',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE Preliminary_Diagnoses (
    diagnosis_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL,
    doctor_id INT NOT NULL,
    preliminary_diagnosis TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE Final_Conclusions (
    conclusion_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL,
    doctor_id INT NOT NULL,
    final_conclusion TEXT NOT NULL,
    recommendations TEXT NULL,
    prescription_id INT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE Prescriptions (
    prescription_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL,
    doctor_id INT NOT NULL,
    patient_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE Prescription_Details (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    medicine_id INT,
    prescription_id INT NOT NULL,
    quantity VARCHAR(100) NOT NULL,
    pills_per_day INT NOT NULL,
    doses_per_day VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Medicines (
    medicine_id INT AUTO_INCREMENT PRIMARY KEY,
    medicine_name VARCHAR(255) NOT NULL,
    usage_instruction VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DELIMITER //

-- Trigger cho khi insert
CREATE TRIGGER update_patient_count_insert
AFTER INSERT ON Appointments
FOR EACH ROW
BEGIN
    IF NEW.status = 'Completed' THEN
        UPDATE Doctors
        SET patient_count = (
            SELECT COUNT(*)
            FROM Appointments
            WHERE doctor_id = NEW.doctor_id
              AND status = 'Completed'
        )
        WHERE doctor_id = NEW.doctor_id;
    END IF;
END;

-- Trigger cho khi update từ trạng thái khác thành Completed
CREATE TRIGGER update_patient_count_update
AFTER UPDATE ON Appointments
FOR EACH ROW
BEGIN
    IF NEW.status = 'Completed' AND OLD.status != 'Completed' THEN
        UPDATE Doctors
        SET patient_count = (
            SELECT COUNT(*)
            FROM Appointments
            WHERE doctor_id = NEW.doctor_id
              AND status = 'Completed'
        )
        WHERE doctor_id = NEW.doctor_id;
    END IF;
END;
//

DELIMITER ;


ALTER TABLE Appointments ADD FOREIGN KEY (patient_id) REFERENCES Patients(patient_id);
ALTER TABLE Appointments ADD FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id);
ALTER TABLE Doctors ADD FOREIGN KEY (specialist_id) REFERENCES Specialists(specialist_id);
ALTER TABLE Notifications ADD FOREIGN KEY (authorization_id) REFERENCES Authorizations(authorization_id) ON DELETE CASCADE;
ALTER TABLE Doctors ADD FOREIGN KEY (authorization_id) REFERENCES Authorizations(authorization_id);
ALTER TABLE Patients ADD FOREIGN KEY (authorization_id) REFERENCES Authorizations(authorization_id);
ALTER TABLE Comments ADD FOREIGN KEY (patient_id) REFERENCES Patients(patient_id) ON DELETE CASCADE;
ALTER TABLE Comments ADD FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id) ON DELETE CASCADE;
ALTER TABLE CommentReplies ADD FOREIGN KEY (comment_id) REFERENCES Comments(comment_id) ON DELETE CASCADE;
ALTER TABLE Waiting_list ADD FOREIGN KEY (patient_id) REFERENCES Patients(patient_id);
ALTER TABLE Waiting_list ADD FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id);
AlTER TABLE WAiting_list ADD FOREIGN KEY (appointment_id) REFERENCES Appointments(appointment_id) ON DELETE SET NULL;
ALTER TABLE Preliminary_Diagnoses ADD FOREIGN KEY (appointment_id) REFERENCES Appointments(appointment_id) ON DELETE CASCADE;
ALTER TABLE Preliminary_Diagnoses ADD FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id) ON DELETE CASCADE;
ALTER TABLE Final_Conclusions ADD FOREIGN KEY (appointment_id) REFERENCES Appointments(appointment_id) ON DELETE CASCADE;
ALTER TABLE Final_Conclusions ADD FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id) ON DELETE CASCADE;
ALTER TABLE Final_Conclusions ADD FOREIGN KEY (prescription_id) REFERENCES Prescriptions(prescription_id) ON DELETE SET NULL;
ALTER TABLE Prescriptions ADD FOREIGN KEY (appointment_id) REFERENCES Appointments(appointment_id) ON DELETE CASCADE;
ALTER TABLE Prescriptions ADD FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id) ON DELETE CASCADE;
ALTER TABLE Prescriptions ADD FOREIGN KEY (patient_id) REFERENCES Patients(patient_id) ON DELETE CASCADE;
ALTER TABLE Prescription_Details ADD FOREIGN KEY (prescription_id) REFERENCES Prescriptions(prescription_id) ON DELETE CASCADE;
ALTER TABLE Prescription_Details ADD FOREIGN KEY (medicine_id) REFERENCES Medicines(medicine_id) ON DELETE CASCADE;