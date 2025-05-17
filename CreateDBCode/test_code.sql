use medicalbooking;

        UPDATE Appointments
        SET status = 'Confirmed'
        WHERE status = 'Pending'
          AND (is_deposit = TRUE OR is_paid = TRUE)