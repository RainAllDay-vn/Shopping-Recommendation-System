package com.project.shoppingrecommendationsystem.models;

import com.opencsv.bean.CsvBindByName;

public abstract class Product {
    private static int counter = 0;
    @CsvBindByName
    final int id;
    @CsvBindByName
    final String name;
    @CsvBindByName
    final String productImage;
    @CsvBindByName
    final int price;
    @CsvBindByName
    final int discountPrice;
    @CsvBindByName
    final String source;
    @CsvBindByName
    final String sourceURL;

    @CsvBindByName
    final String brand;
    @CsvBindByName
    final String color;
    @CsvBindByName
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