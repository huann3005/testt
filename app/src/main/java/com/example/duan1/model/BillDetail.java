package com.example.duan1.model;

public class BillDetail {
    private int id;
    private int idBill;
    private int idProduct;
    private int quantity;
    private int price;
    private String note;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdBill() {
        return idBill;
    }

    public void setIdBill(int idBill) {
        this.idBill = idBill;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BillDetail(int id, int idProduct, int idBill, int quantity, int price, String note) {
        this.id = id;
        this.idBill = idBill;
        this.idProduct = idProduct;
        this.quantity = quantity;
        this.price = price;
        this.note = note;
    }

    public BillDetail() {

    }

    @Override
    public String toString() {
        return "BillDetail{" +
                "id=" + id +
                ", idBill=" + idBill +
                ", idProduct=" + idProduct +
                ", quantity=" + quantity +
                ", price=" + price +
                ", note='" + note + '\'' +
                '}';
    }
}
