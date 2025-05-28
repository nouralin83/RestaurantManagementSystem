package com.example.oop;

public class Employee {
    private int id;
    private String name;
    private String address;
    private String phone;
    private String password;
    private String section;
    private String role;
    Employee(){};
    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getSection() {
        return section;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setRole(String role) {
        this.role = role;
    }
}


