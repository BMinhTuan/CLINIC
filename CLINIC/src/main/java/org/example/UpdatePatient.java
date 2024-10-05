package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UpdatePatient {

    public static void updatePatient() {
        try (Connection connection = DatabaseConnection.getConnection();
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Nhập ID bệnh nhân cần cập nhật: ");
            int patientId = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            // Kiểm tra xem bệnh nhân có mã recurrence_id hay không
            String checkRecurrenceSql = "SELECT r.recurrence_id, r.disease_name, r.recurrence_date," +
                    " r.discharge_re_date, v.discharge_date, v.treatment_cost FROM Disease_Recurrence r " +
                    "JOIN Visit v ON r.visit_id = v.visit_id " +
                    "WHERE v.patient_id = ?";
            PreparedStatement checkRecurrenceStatement = connection.prepareStatement(checkRecurrenceSql);
            checkRecurrenceStatement.setInt(1, patientId);
            ResultSet recurrenceResultSet = checkRecurrenceStatement.executeQuery();

            if (recurrenceResultSet.next()) {
                // Bệnh nhân có mã recurrence_id, có thể cập nhật discharge_re_date
                int recurrenceId = recurrenceResultSet.getInt("recurrence_id");
                String diseaseName = recurrenceResultSet.getString("disease_name");
                String recurrenceDate = recurrenceResultSet.getString("recurrence_date");
                String dischargereDate = recurrenceResultSet.getString("discharge_re_date");

                System.out.println("Bệnh nhân có mã tái phát: " + recurrenceId);
                System.out.println("Bệnh nhân có ngày tái phát: " + recurrenceDate);
                System.out.println("Bệnh nhân có ngày ra viện : " + dischargereDate);
                System.out.println("Bệnh nhân bị bệnh: " + diseaseName);

                System.out.println("Nhập ngày xuất viện tái phát mới (YYYY-MM-DD) hoặc nhấn Enter để bỏ qua: ");
                String dischargeReDate = scanner.nextLine();

                if (!dischargeReDate.isEmpty()) {
                    // Cập nhật discharge_re_date vào bảng Disease_Recurrence
                    String updateRecurrenceSql = "UPDATE Disease_Recurrence SET discharge_re_date = ? WHERE recurrence_id = ?";
                    PreparedStatement updateRecurrenceStatement = connection.prepareStatement(updateRecurrenceSql);
                    updateRecurrenceStatement.setString(1, dischargeReDate);
                    updateRecurrenceStatement.setInt(2, recurrenceId);

                    int rowsUpdated = updateRecurrenceStatement.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Cập nhật ngày xuất viện tái phát thành công!");
                    } else {
                        System.out.println("Không thể cập nhật ngày xuất viện tái phát.");
                    }
                } else {
                    System.out.println("Không có thay đổi nào cho ngày xuất viện tái phát.");
                }

            } else {
                // Bệnh nhân không có mã recurrence_id, có thể cập nhật discharge_date và treatment_cost

                // Lấy thông tin hiện tại từ bảng Visit
                String selectVisitSql = "SELECT entry_date, discharge_date, disease_name, treatment_cost FROM Visit WHERE patient_id = ?";
                PreparedStatement selectVisitStatement = connection.prepareStatement(selectVisitSql);
                selectVisitStatement.setInt(1, patientId);
                ResultSet visitResultSet = selectVisitStatement.executeQuery();

                if (visitResultSet.next()) {
                    String Entrydate = visitResultSet.getString("entry_date");
                    String Disease_name = visitResultSet.getString("disease_name");
                    String currentDischargeDate = visitResultSet.getString("discharge_date");
                    String currentTreatmentCost = visitResultSet.getString("treatment_cost");
                    System.out.println("Ngày vào viện: " + Entrydate);
                    System.out.println("Tên bệnh: " + Disease_name);
                    System.out.println("Ngày xuất viện hiện tại: " + currentDischargeDate);
                    System.out.println("Nhập ngày xuất viện mới (YYYY-MM-DD) hoặc nhấn Enter để giữ nguyên: ");
                    String dischargeDate = scanner.nextLine();
                    if (dischargeDate.isEmpty()) {
                        dischargeDate = currentDischargeDate; // Giữ nguyên nếu không nhập
                    }

                    System.out.println("Chi phí điều trị hiện tại: " + currentTreatmentCost);
                    System.out.println("Nhập chi phí điều trị mới hoặc nhấn Enter để giữ nguyên: ");
                    String treatmentCost = scanner.nextLine();
                    if (treatmentCost.isEmpty()) {
                        treatmentCost = currentTreatmentCost; // Giữ nguyên nếu không nhập
                    }

                    // Cập nhật vào bảng Visit
                    String updateVisitSql = "UPDATE Visit SET discharge_date = ?, treatment_cost = ? WHERE patient_id = ?";
                    PreparedStatement updateVisitStatement = connection.prepareStatement(updateVisitSql);
                    updateVisitStatement.setString(1, dischargeDate);
                    updateVisitStatement.setString(2, treatmentCost);
                    updateVisitStatement.setInt(3, patientId);

                    int rowsUpdated = updateVisitStatement.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Cập nhật thông tin bệnh nhân thành công!");
                    } else {
                        System.out.println("Không thể cập nhật thông tin bệnh nhân.");
                    }
                } else {
                    System.out.println("Không tìm thấy thông tin lần khám của bệnh nhân.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


