package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class ReadPatients {

    public static void readPatients() {
        try (Connection connection = DatabaseConnection.getConnection()) {

            // Truy vấn để lấy thông tin từ các bảng Patient, Visit và Disease_Recurrence
            String sql = "SELECT p.patient_id, p.name, v.visit_id, v.disease_name AS visit_disease, " +
                    "dr.recurrence_id, dr.disease_name AS recurrence_disease " +
                    "FROM Patient p " +
                    "LEFT JOIN Visit v ON p.patient_id = v.patient_id " +
                    "LEFT JOIN Disease_Recurrence dr ON v.visit_id = dr.visit_id " +
                    "ORDER BY p.patient_id, v.visit_id, dr.recurrence_id";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            // Duyệt qua kết quả và hiển thị thông tin bệnh nhân, tên bệnh của các lần khám và tái phát (nếu có)
            while (resultSet.next()) {
                int patientId = resultSet.getInt("patient_id");
                String name = resultSet.getString("name");
                int visitId = resultSet.getInt("visit_id");
                String visitDisease = resultSet.getString("visit_disease");
                int recurrenceId = resultSet.getInt("recurrence_id");
                String recurrenceDisease = resultSet.getString("recurrence_disease");

                // In ra thông tin bệnh nhân và bệnh của mỗi lần khám
                System.out.printf("ID: %d, Tên: %s, Mã khám: %d, Tên bệnh: %s%n",
                        patientId, name, visitId, visitDisease != null ? visitDisease : "N/A");

                // Nếu có tái phát (recurrence_id) thì in thêm thông tin về tái phát
                if (recurrenceId != 0) {
                    System.out.printf("                           Mã tái khám: %d, Tên bệnh tái phát: %s%n",
                            recurrenceId, recurrenceDisease != null ? recurrenceDisease : "N/A");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
