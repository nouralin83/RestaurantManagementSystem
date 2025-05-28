package com.example.oop;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ManagerSignUpController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    public void handleSignUp() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Passwords do not match");
            return;
        }

        // الاتصال بقاعدة البيانات وإضافة المدير
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO managers (name, password) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Manager Registered Successfully");
                    goBack(); // يرجع تلقائيًا بعد التسجيل
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to register manager");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database connection error");
        }
    }

    // ✅ دالة الرجوع إلى واجهة تسجيل الدخول
    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading login page");
        }
    }

    // ✅ دالة مساعدة لإظهار التنبيهات
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.show();
    }
}
