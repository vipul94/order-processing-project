package com.orderManagement.orderProcessing.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Items {

    @JsonProperty(value = "item_name")
    private String itemName;

    @JsonProperty(value = "quantity")
    private int quantity;

    @JsonProperty(value = "price")
    private double price;

    @JsonProperty(value = "total_amount")
    private double totalAmount;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
