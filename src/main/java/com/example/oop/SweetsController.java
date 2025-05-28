package com.example.oop;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.List;

public class SweetsController {

    @FXML
    private TableView<Item> itemsTable;
    @FXML
    private TableColumn<Item, String> itemNameColumn;
    @FXML
    private TableColumn<Item, Double> itemPriceColumn;
    @FXML
    private Button orderButton;
    @FXML
    private CheckBox deliveryCheckBox;
    @FXML
    private TextArea orderSummaryArea;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private ListView<String> orderSummaryList;
    @FXML
    private Button logoutButton;
    @FXML
    private ImageView logoImageView;

    private ObservableList<Item> itemsList = FXCollections.observableArrayList();
    private double totalAccumulatedPrice = 0.0;

    @FXML
    public void initialize() {
        itemNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        itemPriceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        itemsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        loadItems();
        loadAllOrders();

        orderButton.setOnAction(e -> placeOrder());
        logoutButton.setOnAction(e -> logout());

        deliveryCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            // لا توجد طرابيزات، لذا لا حاجة لتعطيل أي شيء هنا
        });
    }

    private void loadItems() {
        itemsList.clear();
        Task<ObservableList<Item>> task = new Task<>() {
            @Override
            protected ObservableList<Item> call() throws Exception {
                ObservableList<Item> tempList = FXCollections.observableArrayList();
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "SELECT * FROM items WHERE section = 'Sweet'";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        tempList.add(new Item(rs.getInt("id"), rs.getString("name"), rs.getDouble("price")));
                    }
                }
                return tempList;
            }
        };

        task.setOnSucceeded(e -> {
            itemsList.setAll(task.getValue());
            itemsTable.setItems(itemsList);
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            showError("Failed to load items.");
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadAllOrders() {
        orderSummaryList.getItems().clear();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "SELECT o.id AS order_id, o.order_time, o.delivery, i.name AS item_name, i.price AS item_price " +
                            "FROM orders o JOIN items i ON o.item_id = i.id WHERE i.section = 'Sweet'";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        int orderId = rs.getInt("order_id");
                        String itemName = rs.getString("item_name");
                        double price = rs.getDouble("item_price");
                        boolean isDelivery = rs.getBoolean("delivery");
                        Timestamp time = rs.getTimestamp("order_time");

                        String summary = String.format("Order ID: %d | Item: %s | Price: %.2f | Delivery: %s | Time: %s",
                                orderId, itemName, price, (isDelivery ? "Yes" : "No"), time.toString());

                        javafx.application.Platform.runLater(() -> orderSummaryList.getItems().add(summary));
                    }
                }
                return null;
            }
        };

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            showError("Failed to load orders.");
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void placeOrder() {
        List<Item> selectedItems = itemsTable.getSelectionModel().getSelectedItems();
        boolean delivery = deliveryCheckBox.isSelected();

        if (!selectedItems.isEmpty()) {
            try (Connection conn = DBConnection.getConnection()) {
                for (Item selectedItem : selectedItems) {
                    String sql = "INSERT INTO orders (item_id, table_id, delivery) VALUES (?, NULL, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, selectedItem.getId());
                    stmt.setBoolean(2, delivery);
                    stmt.executeUpdate();

                    updateOrderSummary(selectedItem, delivery);
                }

                loadAllOrders();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Order placed successfully!");
                alert.show();

            } catch (SQLException e) {
                e.printStackTrace();
                showError("Failed to place order.");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select at least one item to order.");
            alert.show();
        }
    }

    private void updateOrderSummary(Item item, boolean delivery) {
        StringBuilder sb = new StringBuilder();
        sb.append(item.getName()).append(" - ").append(String.format("%.2f", item.getPrice()));
        if (delivery) {
            sb.append(" (Delivery)");
        }
        sb.append("\n");

        totalAccumulatedPrice += item.getPrice();
        totalPriceLabel.setText(String.format("Total Price: %.2f", totalAccumulatedPrice));
        orderSummaryArea.appendText(sb.toString());
    }

    private void logout() {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginPage.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to logout.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
}
