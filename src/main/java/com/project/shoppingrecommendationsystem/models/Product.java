package com.project.shoppingrecommendationsystem.models;

import java.util.List;

public abstract class Product {
    private static int counter = 0;
    final int id;
    final String name;
    final String productImage;
    final int price;
    final int discountPrice;
    final List<Review> reviews;
    final String source;
    final String sourceURL;

    final String brand;
    final String color;
    final List<String[]> description;

    Product(String name, String productImage, int price, int discountPrice, List<Review> reviews, String source, String sourceURL, String brand, String color, List<String[]> description) {
        this.id = ++counter;
        this.name = name;
        this.productImage = productImage;
        this.price = price;
        this.discountPrice = discountPrice;
        this.reviews = reviews;
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
        if(discountPrice == 0) return price;
        return discountPrice;
    }

    public String getSource() {
        return source;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public List<Review> getReviews() {return reviews;}

    public String getBrand() {
        return brand;
    }

    public String getColor() {
        return color;
    }

    public List<String[]> getDescription() {
        return description;
    }

    public abstract boolean match (List<String[]> query);
}