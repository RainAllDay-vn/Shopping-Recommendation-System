package com.project.shoppingrecommendationsystem.models;

public abstract class Product {
    private static int counter = 0;
    final int id;
    final String name;
    final String productImage;
    final int price;
    final int discountPrice;
    final String source;
    final String sourceURL;

    final String brand;
    final String color;
    final String description;

    Product(String name, String productImage, int price, int discountPrice, String source, String sourceURL, String brand, String color, String description) {
        this.id = ++counter;
        this.name = name;
        this.productImage = productImage;
        this.price = price;
        this.discountPrice = discountPrice;
        this.source = source;
        this.sourceURL = sourceURL;
        this.brand = brand;
        this.color = color;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProductImage() {
        return productImage;
    }

    public int getPrice() {
        return price;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public String getSource() {
        return source;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public String getBrand() {
        return brand;
    }

    public String getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }
}