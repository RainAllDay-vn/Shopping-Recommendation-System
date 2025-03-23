package com.project.shoppingrecommendationsystem.models;

public abstract class Product {
    private static int counter = 0;
    private final int id;
    private final String name;
    private final String productImage;
    private final int price;
    private final int discountPrice;
    private final String sourceURL;

    private final String brand;
    private final String color;
    private final String description;

    Product(String name, String productImage, int price, int discountPrice, String sourceURL, String brand, String color, String description) {
        this.id = ++counter;
        this.name = name;
        this.productImage = productImage;
        this.price = price;
        this.discountPrice = discountPrice;
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
