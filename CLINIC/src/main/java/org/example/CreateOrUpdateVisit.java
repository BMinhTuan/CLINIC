package org.example;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

    public class CreateOrUpdateVisit {

        public static void addVisitOrRecurrence() {
            try (Connection connection = DatabaseConnection.getConnection();
                 Scanner scanner = new Scanner(System.in)) {

                System.out.println("Nhập mã số quốc gia (national_id): ");
                String nationalId = scanner.nextLine();

                System.out.println("Nhập ngày nhập viện (YYYY-MM-DD): ");
                String entryDateStr = scanner.nextLine();
                LocalDate entryDate = LocalDate.parse(entryDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                System.out.println("Nhập tên bệnh: ");
                String diseaseName = scanner.nextLine();

                // Kiểm tra xem bệnh nhân đã tồn tại chưa
                String checkSql = "SELECT patient_id FROM Patient WHERE national_id = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkSql);
                checkStatement.setString(1, nationalId);
                ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next()) {
                    int patientId = resultSet.getInt("patient_id");

                    // Kiểm tra lần khám trước của bệnh nhân
                    String previousVisitSql = "SELECT visit_id, disease_name, discharge_date FROM Visit WHERE patient_id = ? ORDER BY discharge_date DESC LIMIT 1";
                    PreparedStatement previousVisitStatement = connection.prepareStatement(previousVisitSql);
                    previousVisitStatement.setInt(1, patientId);
                    ResultSet previousVisitResult = previousVisitStatement.executeQuery();

                    if (previousVisitResult.next()) {
                        int visitId = previousVisitResult.getInt("visit_id");
                        String previousDiseaseName = previousVisitResult.getString("disease_name");
                        LocalDate dischargeDate = previousVisitResult.getDate("discharge_date").toLocalDate();

                        // So sánh disease_name và tháng của entry_date và discharge_date
                        if (previousDiseaseName.equals(diseaseName) && dischargeDate.getMonth().equals(entryDate.getMonth())) {
                            // Thêm vào bảng Disease_recurrence
                            String recurrenceSql = "INSERT INTO Disease_recurrence (visit_id, recurrence_date, disease_name) VALUES (?, ?, ?)";
                            PreparedStatement recurrenceStatement = connection.prepareStatement(recurrenceSql);
                            recurrenceStatement.setInt(1, visitId);
                            recurrenceStatement.setString(2, entryDateStr);
                            recurrenceStatement.setString(3, diseaseName);

                            int rowsInserted = recurrenceStatement.executeUpdate();
                            if (rowsInserted > 0) {
                                System.out.println("Đã thêm lần tái phát bệnh thành công!");
                            }

                        } else {
                            // Thêm lần khám mới vào bảng Visit
                            addNewVisit(connection, patientId, diseaseName, entryDateStr, scanner);
                        }

                    } else {
                        // Nếu không có lần khám trước, thêm lần khám mới
                        addNewVisit(connection, patientId, diseaseName, entryDateStr, scanner);
                    }

                } else {
                    // Bệnh nhân chưa tồn tại, thêm thông tin bệnh nhân mới và lần khám
                    addNewPatientAndVisit(connection, nationalId, diseaseName, entryDateStr, scanner);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Hàm thêm thông tin lần khám mới vào bảng Visit
        private static void addNewVisit(Connection connection, int patientId, String diseaseName, String entryDate, Scanner scanner) throws SQLException {
            System.out.println("Chọn mã bác sĩ phụ trách (21-40): ");
            String doctorId = scanner.nextLine();

            String visitSql = "INSERT INTO Visit (patient_id, doctor_id, entry_date, disease_name) VALUES (?, ?, ?, ?)";
            PreparedStatement visitStatement = connection.prepareStatement(visitSql);
            visitStatement.setInt(1, patientId);
            visitStatement.setInt(2, Integer.parseInt(doctorId));
            visitStatement.setString(3, entryDate);
            visitStatement.setString(4, diseaseName);

            int visitInserted = visitStatement.executeUpdate();
            if (visitInserted > 0) {
                System.out.println("Đã thêm lần khám thành công!");
            }
        }

        // Hàm thêm bệnh nhân mới và thêm lần khám đầu tiên của bệnh nhân
        private static void addNewPatientAndVisit(Connection connection, String nationalId, String diseaseName, String entryDate, Scanner scanner) throws SQLException {
            System.out.println("Nhập tên bệnh nhân: ");
            String name = scanner.nextLine();

            System.out.println("Nhập ngày sinh (YYYY-MM-DD): ");
            String dateOfBirth = scanner.nextLine();

            System.out.println("Nhập địa chỉ: ");
            String address = scanner.nextLine();

            System.out.println("Nhập số điện thoại: ");
            String phoneNumber = scanner.nextLine();

            System.out.println("Chọn mã bác sĩ phụ trách (21-40): ");
            String doctorId = scanner.nextLine();

            // Thêm bệnh nhân mới vào bảng Patient
            String patientSql = "INSERT INTO Patient (national_id, name, date_of_birth, address, phone_number) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement patientStatement = connection.prepareStatement(patientSql, PreparedStatement.RETURN_GENERATED_KEYS);
            patientStatement.setString(1, nationalId);
            patientStatement.setString(2, name);
            patientStatement.setString(3, dateOfBirth);
            patientStatement.setString(4, address);
            patientStatement.setString(5, phoneNumber);

            int rowsInserted = patientStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Đã thêm bệnh nhân mới thành công!");
                ResultSet generatedKeys = patientStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int patientId = generatedKeys.getInt(1);

                    // Sau khi thêm bệnh nhân mới, thêm lần khám vào bảng Visit
                    addNewVisit(connection, patientId, diseaseName, entryDate, scanner);
                }
            }
        }
    }


