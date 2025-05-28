package com.example.oop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

public class ManagerController {
    @FXML
    private ListView<String> employeeListView;
    @FXML
    private ListView<String> itemListView;
    @FXML
    private ListView<String> topSellingListView;

    @FXML
    public void initialize() {
        loadDataAsync("SELECT * FROM employees", rs -> {
            try {
                return rs.getString("name");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, employeeListView);
        loadDataAsync("SELECT * FROM items",
                rs -> {
                    try {
                        return rs.getString("name") + " - " + rs.getDouble("price");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                itemListView);
        loadDataAsync("SELECT name FROM items ORDER BY sales_count DESC LIMIT 5",
                rs -> {
                    try {
                        return rs.getString("name");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                topSellingListView);
    }

    private <T> void loadDataAsync(String query, Function<ResultSet, T> mapper, ListView<T> listView) {
        Task<ObservableList<T>> task = new Task<>() {
            @Override
            protected ObservableList<T> call() throws Exception {
                ObservableList<T> resultList = FXCollections.observableArrayList();
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query);
                     ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {
                        resultList.add(mapper.apply(rs));
                    }
                }
                return resultList;
            }
        };

        task.setOnSucceeded(e -> listView.setItems(task.getValue()));
        task.setOnFailed(e -> task.getException().printStackTrace());

        Thread thread = new Thread(task);
        thread.setDaemon(true); // ينهى الخيط تلقائيًا عند غلق التطبيق
        thread.start();
    }

    @FXML
    public void addEmployee() {
        switchScene("addEmployee.fxml", employeeListView);
    }

    @FXML
    public void addItem() {
        switchScene("addItem.fxml", itemListView);
    }

    @FXML
    public void goToLogin() {
        switchScene("LoginPage.fxml", topSellingListView);
    }

    private void switchScene(String fxmlFile, ListView<?> sourceListView) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) sourceListView.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
