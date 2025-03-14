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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setDescription(String description) {
        this.description = description;
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
