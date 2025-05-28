package com.example.oop;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class EmployeeController {
    @FXML
    private ListView<String> itemListView;

    @FXML
    public void initialize() {
        // Populate items based on the employee's section (Sweets or Savory)
        String section = "Sweets"; // You can get the section based on login data

        if (section.equals("Sweets")) {
            itemListView.getItems().addAll("Cake", "Ice Cream");
        } else if (section.equals("Savory")) {
            itemListView.getItems().addAll("Pizza", "Burger");
        }
    }
}
