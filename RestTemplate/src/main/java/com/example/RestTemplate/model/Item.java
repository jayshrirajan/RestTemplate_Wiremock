package com.example.RestTemplate.model;

public class Item {

    private int idItem;
    private String itemCode;
    private String itemName;
    private int quantity;
    private double itemCost;


    @Override
    public String toString() {
        return "Item{" +
                "idItem=" + idItem +
                ", itemCode='" + itemCode + '\'' +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", itemCost=" + itemCost +
                '}';
    }
    public Item(){

    }

    public Item(int idItem, String itemCode, String itemName, int quantity, double itemCost) {
        this.idItem = idItem;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.quantity = quantity;
        this.itemCost = itemCost;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

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

    public double getItemCost() {
        return itemCost;
    }

    public void setItemCost(double itemCost) {
        this.itemCost = itemCost;
    }


}
