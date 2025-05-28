package com.example.oop;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;

public class OrderController {
    @FXML
    private ComboBox<String> orderStatusCombo;

    @FXML
    public void initialize() {
        orderStatusCombo.getItems().addAll("Preparing", "In Transit", "Delivered");
    }

    @FXML
    public void updateOrderStatus() {
        String status = orderStatusCombo.getValue();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Order status updated to: " + status);
        alert.show();
    }
}
