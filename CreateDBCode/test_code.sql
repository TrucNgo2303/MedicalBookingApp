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
	r.reply_id,
	r.reply_detail,
	r.created_at,
	p.full_name AS patient_name,
	p.avatar AS patient_avatar
FROM CommentReplies r
JOIN Patients p ON r.user_id = p.patient_id
WHERE r.comment_id = 1;

SELECT patient_id FROM Patients WHERE authorization_id = 18

