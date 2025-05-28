package com.example.oop;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.IOException;
import java.util.Optional;
import javafx.scene.control.TextInputDialog;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection conn = DBConnection.getConnection()) {
            // تحقق من المدير
            String managerSql = "SELECT * FROM managers WHERE name = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(managerSql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    openManagerDashboard();
                    return;
                }
            }

            // تحقق من الموظف
            String employeeSql = "SELECT * FROM employees WHERE name = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(employeeSql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String section = rs.getString("section"); // جلب القسم
                    openEmployeeDashboard(section); // فتح الواجهة بناء على القسم
                    return;
                }
            }

            // لو البيانات غلط
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Invalid credentials");
            alert.setContentText("Username or password is incorrect");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showSignUpPrompt() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Manager Sign Up");
        dialog.setHeaderText("Enter the Manager Sign Up Code");
        dialog.setContentText("Code:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && result.get().equals("1234")) {
            openManagerSignUp();
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Incorrect Code");
            alert.setContentText("The code you entered is incorrect.");
            alert.showAndWait();
        }
    }

    private void openManagerSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ManagerSignUp.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load the manager sign-up page.");
        }
    }

    private void openManagerDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("managerDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load the manager dashboard.");
        }
    }

    private void openEmployeeDashboard(String section) {
        try {
            FXMLLoader loader;
            if (section.equalsIgnoreCase("Sweet")) {
                loader = new FXMLLoader(getClass().getResource("sweets.fxml"));
            } else if (section.equalsIgnoreCase("Savory")) {
                loader = new FXMLLoader(getClass().getResource("savory.fxml"));
            } else {
                showError("Unknown section: " + section);
                return;
            }

            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load the employee dashboard.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Loading Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
