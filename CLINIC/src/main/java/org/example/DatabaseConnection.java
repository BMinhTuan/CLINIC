package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/clinic"; // Đổi "medical_system" thành tên cơ sở dữ liệu của bạn
    private static final String USER = "root"; // Tên đăng nhập MySQL, thông thường là "root"
    private static final String PASSWORD = "Buiminhtuan2@"; // Mật khẩu MySQL
    public static void main(String[] args){
        getConnection();
        //CreateOrUpdateVisit.addVisitOrRecurrence();
        //ReadPatients.readPatients();
        //UpdatePatient.updatePatient();
        AddPrescription.addPrescription();
    }
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Đăng ký MySQL JDBC driver (tự động nếu dùng MySQL Connector/J 8.0 trở lên)
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Kết nối cơ sở dữ liệu thành công!");
        } catch (SQLException e) {
            System.out.println("Kết nối cơ sở dữ liệu thất bại!");
            e.printStackTrace();
        }
        return connection;
    }
}

