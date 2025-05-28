package com.example.oop;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddItemController {
    @FXML
    private TextField itemNameField;
    @FXML
    private TextField itemPriceField;
    @FXML
    private ComboBox<String> itemCategoryCombo;

    @FXML
    public void initialize() {
        itemCategoryCombo.getItems().addAll("Sweet", "Savory");
    }

    @FXML
    public void addItem() {
        String name = itemNameField.getText();
        String priceText = itemPriceField.getText();
        String category = itemCategoryCombo.getValue();

        if (name.isEmpty() || priceText.isEmpty() || category == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please fill in all fields");
            alert.show();
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO items (name, price, section) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, name);
                    stmt.setDouble(2, price);
                    stmt.setString(3, category);

                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText("Item added successfully");
                        alert.show();
                        itemNameField.clear();
                        itemPriceField.clear();
                        itemCategoryCombo.setValue(null);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Failed to add item");
                        alert.show();
                    }
                }
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid price format");
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Database error");
            alert.show();
        }
    }

    // ✅ دالة الرجوع إلى الواجهة السابقة
    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("managerDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) itemNameField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error loading previous page");
            alert.show();
        }
    }
}
