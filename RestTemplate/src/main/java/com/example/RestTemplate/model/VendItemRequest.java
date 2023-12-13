package com.example.RestTemplate.model;

public class VendItemRequest {
    private int itemId;
    private double amount;
    private int quantity;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public double getAmount() {
        return amount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "VendItemRequest{" +
                "itemId=" + itemId +
                ", amount=" + amount +
                '}';
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
