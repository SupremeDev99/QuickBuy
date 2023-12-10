package com.assignment.quickbuy.Model;

public class Cart {
    private String pid;
    private String pname;
    private String image;
    private double price;
    private int quantity;

    public Cart() {
        // Default constructor required for Firebase
    }

    public Cart(String pid, String pname, String image, double price, int quantity) {
        this.pid = pid;
        this.pname = pname;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
