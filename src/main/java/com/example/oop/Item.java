package com.example.oop;

import javafx.beans.property.*;

public class Item {
    private final Integer id;
    private final StringProperty name;
    private final DoubleProperty price;

    public Item(int id, String name, double price) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
    }

    public int getId() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public String getName() {
        return name.get();
    }

    public double getPrice() {
        return price.get();
    }
}

