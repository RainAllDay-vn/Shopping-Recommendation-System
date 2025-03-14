package com.project.shoppingrecommendationsystem.models;

public abstract class Product {
    private int id;
    private String name;
    private String description;
    private int price;
    private String sourceURL;

    public Product(int id, String name, String description, int price, String sourceURL) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.sourceURL = sourceURL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", sourceURL='" + sourceURL + '\'' +
                '}';
    }
}
