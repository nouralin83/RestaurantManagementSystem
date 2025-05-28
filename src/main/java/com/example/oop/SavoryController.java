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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SavoryController {

    @FXML
    private TableView<Table> tablesTable;
    @FXML
    private TableColumn<Table, Integer> tableNumberColumn;
    @FXML
    private TableColumn<Table, String> statusColumn;
    @FXML
    private Button reserveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button logoutButton;
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
    private ImageView logoImageView;

    private ObservableList<Table> tablesList = FXCollections.observableArrayList();
    private ObservableList<Item> itemsList = FXCollections.observableArrayList();
    private double totalAccumulatedPrice = 0.0;

    @FXML
    public void initialize() {
        tableNumberColumn.setCellValueFactory(cellData -> cellData.getValue().tableNumberProperty().asObject());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        itemNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        itemPriceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        itemsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        loadTables();
        loadItems();
        loadAllOrders();

        reserveButton.setOnAction(e -> reserveTable());
        cancelButton.setOnAction(e -> cancelReservation());
        orderButton.setOnAction(e -> placeOrder());
        logoutButton.setOnAction(e -> logout());

        deliveryCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                tablesTable.setDisable(true);
                tablesTable.getSelectionModel().clearSelection();
            } else {
                tablesTable.setDisable(false);
            }
        });
    }

    private void loadTables() {
        tablesList.clear();
        Task<ObservableList<Table>> task = new Task<>() {
            @Override
            protected ObservableList<Table> call() throws Exception {
                ObservableList<Table> tempList = FXCollections.observableArrayList();
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "SELECT * FROM tables";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        boolean isReserved = rs.getBoolean("is_reserved");
                        String status = isReserved ? "Reserved" : "Available";
                        tempList.add(new Table(rs.getInt("id"), rs.getInt("table_number"), status));
                    }
                }
                return tempList;
            }
        };

        task.setOnSucceeded(e -> {
            tablesList.setAll(task.getValue());
            tablesTable.setItems(tablesList);
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            showError("Failed to load tables.");
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadItems() {
        itemsList.clear();
        Task<ObservableList<Item>> task = new Task<>() {
            @Override
            protected ObservableList<Item> call() throws Exception {
                ObservableList<Item> tempList = FXCollections.observableArrayList();
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "SELECT * FROM items WHERE section = 'Savory'";
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
                            "FROM orders o JOIN items i ON o.item_id = i.id";
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

                        // استخدم runLater لتحديث واجهة المستخدم
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

    private void reserveTable() {
        Table selectedTable = tablesTable.getSelectionModel().getSelectedItem();
        if (selectedTable != null && selectedTable.getStatus().equals("Available")) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE tables SET is_reserved = TRUE WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, selectedTable.getId());
                stmt.executeUpdate();
                selectedTable.setStatus("Reserved");
                tablesTable.refresh();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Table reserved successfully!");
                alert.show();
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Failed to reserve table.");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select an available table to reserve.");
            alert.show();
        }
    }

    private void cancelReservation() {
        Table selectedTable = tablesTable.getSelectionModel().getSelectedItem();
        if (selectedTable != null && selectedTable.getStatus().equals("Reserved")) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE tables SET is_reserved = FALSE WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, selectedTable.getId());
                stmt.executeUpdate();
                selectedTable.setStatus("Available");
                tablesTable.refresh();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Table reservation cancelled.");
                alert.show();
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Failed to cancel reservation.");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select a reserved table to cancel.");
            alert.show();
        }
    }

    private void placeOrder() {
        List<Item> selectedItems = itemsTable.getSelectionModel().getSelectedItems();
        boolean delivery = deliveryCheckBox.isSelected();
        Table selectedTable = tablesTable.getSelectionModel().getSelectedItem();

        if (!selectedItems.isEmpty()) {
            if (!delivery && selectedTable == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Please select a table for the order.");
                alert.show();
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                for (Item selectedItem : selectedItems) {
                    String sql = "INSERT INTO orders (item_id, table_id, delivery) VALUES (?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, selectedItem.getId());
                    if (delivery) {
                        stmt.setNull(2, java.sql.Types.INTEGER);
                    } else {
                        stmt.setInt(2, selectedTable.getId());
                    }
                    stmt.setBoolean(3, delivery);
                    stmt.executeUpdate();

                    updateOrderSummary(selectedItem, selectedTable, delivery);
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

    private void updateOrderSummary(Item item, Table table, boolean delivery) {
        StringBuilder sb = new StringBuilder();
        sb.append(item.getName()).append(" - ").append(String.format("%.2f", item.getPrice()));
        if (delivery) {
            sb.append(" (Delivery)");
        } else if (table != null) {
            sb.append(" (Table ").append(table.getTableNumber()).append(")");
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
