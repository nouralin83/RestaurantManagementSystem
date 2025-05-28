package com.example.oop;

import java.util.Date;

public class Order {
    private int id;
    private int employeeId;
    private int itemId;
    private String status;
    private Date orderTime;
    private Date deliveryTime;
    Order(){};
    // Getters
    public int getId() {
        return id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getStatus() {
        return status;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}
