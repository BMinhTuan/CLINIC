package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("------------------------------------------------------------------------------------");
            System.out.println("---- MENU ----");
            System.out.println("1. Thêm hồ sơ bệnh nhân");
            System.out.println("2. Cập nhật hồ sơ bệnh nhân");
            System.out.println("3. Danh sách thông tin bệnh nhân");
            System.out.println("0. Thoát");
            System.out.print("Chọn một tùy chọn: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Đọc bỏ newline

            switch (choice) {
                case 1:
                    // Gọi phương thức để thêm hồ sơ bệnh nhân
                    CreateOrUpdateVisit.addVisitOrRecurrence();
                    break;
                case 2:
                    // Gọi phương thức để cập nhật hồ sơ bệnh nhân
                    UpdatePatient.updatePatient();
                    break;
                case 3:
                    // Gọi phương thức để hiển thị danh sách bệnh nhân
                    ReadPatients.readPatients();
                    break;
                case 0:
                    // Thoát khỏi chương trình
                    running = false;
                    System.out.println("Đã thoát khỏi chương trình.");
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng chọn lại.");
            }
        }

        scanner.close();
    }
}
