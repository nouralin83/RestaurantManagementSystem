package com.example.oop;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddEmployeeController {
    @FXML
    private TextField employeeNameField;
    @FXML
    private TextField employeeAddressField;
    @FXML
    private TextField employeePhoneField;
    @FXML
    private TextField employeePasswordField;
    @FXML
    private ComboBox<String> employeeSectionCombo;

    @FXML
    public void initialize() {
        employeeSectionCombo.getItems().addAll("Sweet", "Savory");
    }

    @FXML
    public void addEmployee() {
        // الحصول على القيم المدخلة
        String name = employeeNameField.getText();
        String address = employeeAddressField.getText();
        String phone = employeePhoneField.getText();
        String password = employeePasswordField.getText();
        String section = employeeSectionCombo.getValue();

        // تحقق من الإدخالات قبل الإضافة
        if (name.isEmpty() || address.isEmpty() || phone.isEmpty() || password.isEmpty() || section == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please fill in all fields");
            alert.show();
        } else {
            // الاتصال بقاعدة البيانات وإضافة الموظف
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO employees (name, address, phone, password, section) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, name);
                    stmt.setString(2, address);
                    stmt.setString(3, phone);
                    stmt.setString(4, password);
                    stmt.setString(5, section);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText("Employee added successfully");
                        alert.show();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Failed to add employee");
                        alert.show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Error connecting to database");
                alert.show();
            }
        }
    }

    // إضافة طريقة العودة
    @FXML
    public void goBack() {
        try {
            // تحميل الصفحة السابقة (مثال: صفحة الـ Dashboard الخاصة بالمدير)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("managerDashboard.fxml"));
            Parent root = loader.load();

            // الحصول على الـ Stage الحالي
            Stage stage = (Stage) employeeNameField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600)); // تحديد حجم النافذة

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error loading previous page");
            alert.show();
        }
    }
}
