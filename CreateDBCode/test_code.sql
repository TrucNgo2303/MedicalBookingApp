use medicalbooking;

SELECT d.doctor_id, d.full_name, d.phone_number, d.specialty, d.qualification, d.experience_years, d.summary, d.avatar,s.specialist_name, COALESCE(AVG(c.star), 0) AS average_star
FROM Doctors d
JOIN Specialists s ON d.specialist_id = s.specialist_id
LEFT JOIN Comments c ON d.doctor_id = c.doctor_id
WHERE s.specialist_name = 'Nhi khoa'
GROUP BY d.doctor_id;


SELECT d.*, COALESCE(AVG(c.star), 0) AS average_star, COUNT(c.comment_id) AS total_comments
FROM Doctors d
LEFT JOIN Comments c ON d.doctor_id = c.doctor_id
WHERE d.doctor_id = 1;


use quanlyhaisan;
INSERT INTO haisan (ten, gia, mota) VALUES
('Tôm hùm', 1500000, 'Tôm hùm tươi sống, size lớn'),
('Cua hoàng đế', 2500000, 'Cua hoàng đế nhập khẩu từ Alaska'),
('Mực ống', 300000, 'Mực ống tươi, loại 1'),
('Cá hồi', 500000, 'Cá hồi Na Uy, giàu Omega-3'),
('Nghêu', 100000, 'Nghêu sạch, giàu canxi');
SELECT ten, gia, mota FROM haisan;

SELECT 
    s.specialist_id, 
    s.specialist_name,
    s.icon,
    s.consultation_fee,
    COUNT(d.doctor_id) AS numberOfDoctor
FROM Specialists s
LEFT JOIN Doctors d ON s.specialist_id = d.specialist_id
GROUP BY s.specialist_id, s.specialist_name;

SELECT 
    d.doctor_id,
    d.full_name,
    d.specialty,
    d.qualification,
    d.avatar,
    COALESCE(AVG(c.star), 0) AS average_star
FROM Doctors d
LEFT JOIN Comments c ON d.doctor_id = c.doctor_id
WHERE d.specialist_id = 1 
GROUP BY d.doctor_id;

SELECT 
    a.appointment_date,
    a.appointment_time
FROM 
    Appointments a
WHERE doctor_id = 1;

SELECT
	c.comment_id,
	c.comment_detail,
	c.star,
	c.created_at,
	p.full_name AS patient_name,
	p.avatar AS patient_avatar
FROM Comments c
JOIN Patients p ON c.patient_id = p.patient_id
WHERE c.doctor_id = 1;

SELECT
    p.full_name AS patient_name,
    d.full_name AS doctor_name,
    d.specialty,
    DATE(a.appointment_datetime) AS appointment_date,
    TIME(a.appointment_datetime) AS appointment_time,
    a.reason,
    a.status,
    a.is_online,
    a.consultation_fee,
    EXISTS (
        SELECT 1 FROM Preliminary_Diagnoses pd
        WHERE pd.appointment_id = a.appointment_id
    ) AS has_preliminary_diagnosis,
    EXISTS (
        SELECT 1 FROM Prescriptions pr
        WHERE pr.appointment_id = a.appointment_id
    ) AS has_prescription
FROM Appointments a
JOIN Patients p ON a.patient_id = p.patient_id
JOIN Doctors d ON a.doctor_id = d.doctor_id
WHERE a.appointment_id = 1;

DELETE FROM Preliminary_Diagnoses WHERE appointment_id = 1;


SELECT d.full_name
FROM Doctors d
JOIN Specialists s ON d.specialist_id = s.specialist_id
WHERE s.specialist_name = 'Nội tổng quát'
  AND d.doctor_id NOT IN (
    SELECT a.doctor_id
    FROM Appointments a
    WHERE a.appointment_datetime = '2025-04-25 10:00:00'
      AND a.status != 'Cancelled'
  );


