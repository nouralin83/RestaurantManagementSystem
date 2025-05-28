package com.example.oop;
import javafx.beans.property.*;

public class Table {
    private final IntegerProperty tableNumber;
    private final StringProperty status;
    private final int id;

    public Table(int id, int tableNumber, String status) {
        this.id = id;
        this.tableNumber = new SimpleIntegerProperty(tableNumber);
        this.status = new SimpleStringProperty(status);
    }

    public int getId() {
        return id;
    }

    public IntegerProperty tableNumberProperty() {
        return tableNumber;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public int getTableNumber() {
        return tableNumber.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getStatus() {
        return status.get();
    }
}

