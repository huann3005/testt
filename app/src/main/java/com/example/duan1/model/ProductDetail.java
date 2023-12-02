package com.example.duan1.model;

public class ProductDetail {
    private int id;
    private int idProduct;
    private String description;
    private String image;

    public ProductDetail(int id, int idProduct, String description, String image) {
        this.id = id;
        this.idProduct = idProduct;
        this.description = description;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductDetail() {
    }
}
