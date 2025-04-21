-- Thêm qualification
UPDATE Doctors
SET qualification = CASE 
    WHEN doctor_id = 1 THEN 'Bác sĩ chuyên khoa I'
    WHEN doctor_id = 2 THEN 'Bác sĩ chuyên khoa II'
    WHEN doctor_id = 3 THEN 'Chứng chỉ nhi khoa nâng cao'
    WHEN doctor_id = 4 THEN 'Bác sĩ chuyên khoa I'
    WHEN doctor_id = 5 THEN 'Chứng chỉ tim mạch can thiệp'
    WHEN doctor_id = 6 THEN 'Chứng chỉ nội soi tai - mũi - họng'
    WHEN doctor_id = 7 THEN 'Chứng chỉ da liễu thẩm mỹ'
    WHEN doctor_id = 8 THEN 'Tiến sĩ Y học'
    WHEN doctor_id = 9 THEN 'Chứng chỉ phẫu thuật nội soi'
    WHEN doctor_id = 10 THEN 'Thạc sĩ Y học'
    WHEN doctor_id = 11 THEN 'Chứng chỉ sản phụ khoa nâng cao'
    WHEN doctor_id = 12 THEN 'Bác sĩ chuyên khoa II'
    WHEN doctor_id = 13 THEN 'Bác sĩ chuyên khoa I'
    WHEN doctor_id = 14 THEN 'Chứng chỉ da liễu thẩm mỹ'
    WHEN doctor_id = 15 THEN 'Chứng chỉ nội khoa nâng cao'
    ELSE qualification  -- Giữ nguyên avatar nếu không nằm trong danh sách
END
WHERE doctor_id BETWEEN 1 AND 15;

-- Thêm summary
UPDATE Doctors
SET summary = CASE 
    WHEN doctor_id = 1 THEN 'Bác sĩ nội tổng quát với 10 năm kinh nghiệm trong điều trị các bệnh lý mãn tính như tiểu đường, cao huyết áp. Luôn tận tâm với bệnh nhân.'
    WHEN doctor_id = 2 THEN 'Bác sĩ ngoại tổng quát với 8 năm kinh nghiệm, chuyên thực hiện các phẫu thuật nội soi, điều trị chấn thương và các bệnh lý ngoại khoa.'
    WHEN doctor_id = 3 THEN 'Bác sĩ nhi khoa với 12 năm kinh nghiệm, tận tâm chăm sóc sức khỏe trẻ em, giúp các bé phát triển khỏe mạnh ngay từ những năm đầu đời.'
    WHEN doctor_id = 4 THEN 'Bác sĩ sản phụ khoa với 15 năm kinh nghiệm, chuyên theo dõi thai kỳ, chăm sóc sức khỏe sinh sản và hỗ trợ sinh nở an toàn.'
    WHEN doctor_id = 5 THEN 'Bác sĩ tim mạch với 7 năm kinh nghiệm, chuyên điều trị bệnh mạch vành, tăng huyết áp, rối loạn nhịp tim và các bệnh lý tim mạch khác.'
    WHEN doctor_id = 6 THEN 'Bác sĩ tai - mũi - họng với 9 năm kinh nghiệm, chuyên nội soi, điều trị viêm xoang, viêm họng mãn tính và phẫu thuật chỉnh hình tai mũi họng.'
    WHEN doctor_id = 7 THEN 'Bác sĩ da liễu với 11 năm kinh nghiệm, chuyên điều trị mụn, nám, tàn nhang và các bệnh lý về da liễu thẩm mỹ, giúp bệnh nhân có làn da khỏe mạnh.'
    WHEN doctor_id = 8 THEN 'Bác sĩ nội tổng quát với 6 năm kinh nghiệm, chuyên chẩn đoán và điều trị các bệnh lý nội khoa phức tạp, giúp bệnh nhân cải thiện sức khỏe toàn diện.'
    WHEN doctor_id = 9 THEN 'Bác sĩ ngoại tổng quát với 14 năm kinh nghiệm, chuyên thực hiện các phẫu thuật nội soi tiên tiến, giúp bệnh nhân phục hồi nhanh chóng.'
    WHEN doctor_id = 10 THEN 'Bác sĩ nhi khoa với 5 năm kinh nghiệm, tận tâm đồng hành cùng phụ huynh trong quá trình chăm sóc và phát triển sức khỏe của trẻ nhỏ.'
    WHEN doctor_id = 11 THEN 'Bác sĩ sản phụ khoa với 13 năm kinh nghiệm, chuyên điều trị hiếm muộn, theo dõi thai kỳ và hỗ trợ sinh sản an toàn cho mẹ và bé.'
    WHEN doctor_id = 12 THEN 'Bác sĩ tim mạch với 10 năm kinh nghiệm, chuyên điều trị suy tim, rối loạn nhịp tim và tư vấn lối sống lành mạnh để bảo vệ trái tim.'
    WHEN doctor_id = 13 THEN 'Bác sĩ tai - mũi - họng với 8 năm kinh nghiệm, chuyên điều trị viêm tai giữa, viêm xoang mãn tính và các bệnh lý hô hấp trên.'
    WHEN doctor_id = 14 THEN 'Bác sĩ da liễu với 9 năm kinh nghiệm, chuyên chăm sóc da thẩm mỹ, điều trị nám, sẹo rỗ và giúp khách hàng có làn da sáng khỏe.'
    WHEN doctor_id = 15 THEN 'Bác sĩ nội tổng quát với 7 năm kinh nghiệm, chuyên điều trị các bệnh lý chuyển hóa như béo phì, rối loạn lipid máu và các bệnh mãn tính.'
    ELSE summary  -- Giữ nguyên giá trị hiện tại nếu không nằm trong danh sách
END
WHERE doctor_id BETWEEN 1 AND 15;

UPDATE Specialists
SET summary = CASE 
    WHEN specialist_id = 1 THEN 'Chuyên khám và điều trị các bệnh lý nội khoa như tăng huyết áp, tiểu đường, rối loạn tiêu hóa, bệnh lý gan, thận và các bệnh mãn tính khác.'
    WHEN specialist_id = 2 THEN 'Chuyên thực hiện các phẫu thuật tổng quát như cắt ruột thừa, thoát vị bẹn, phẫu thuật dạ dày, đại tràng, xử lý vết thương và chấn thương phần mềm.'
    WHEN specialist_id = 3 THEN 'Chuyên khám và điều trị các bệnh lý nhi khoa như viêm phổi, suy dinh dưỡng, sốt xuất huyết, dị ứng, tiêm chủng và theo dõi sự phát triển của trẻ.'
    WHEN specialist_id = 4 THEN 'Chăm sóc sức khỏe sinh sản cho phụ nữ, theo dõi thai kỳ, điều trị rối loạn kinh nguyệt, u xơ tử cung, u nang buồng trứng và hỗ trợ sinh sản.'
    WHEN specialist_id = 5 THEN 'Chuyên khám và điều trị các bệnh lý về tim như cao huyết áp, bệnh mạch vành, suy tim, rối loạn nhịp tim và tư vấn phòng ngừa đột quỵ.'
    WHEN specialist_id = 6 THEN 'Khám và điều trị các bệnh lý như viêm xoang, viêm họng, viêm tai giữa, mất thính lực, dị vật đường thở và phẫu thuật tai mũi họng.'
    WHEN specialist_id = 7 THEN 'Chẩn đoán và điều trị các bệnh về da như viêm da, mụn trứng cá, nám, tàn nhang, nhiễm nấm, bệnh chàm, lão hóa da và các bệnh lây truyền qua đường tình dục.'
    ELSE summary 
END
WHERE specialist_id BETWEEN 1 AND 7;

UPDATE Specialists
SET consultation_fee = CASE 
    WHEN specialist_id = 1 THEN '300000'
    WHEN specialist_id = 2 THEN '400000'
    WHEN specialist_id = 3 THEN '250000'
    WHEN specialist_id = 4 THEN '350000'
    WHEN specialist_id = 5 THEN '500000'
    WHEN specialist_id = 6 THEN '200000'
    WHEN specialist_id = 7 THEN '280000'
    ELSE consultation_fee 
END
WHERE specialist_id BETWEEN 1 AND 7;