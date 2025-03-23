package com.project.shoppingrecommendationsystem.models;

public abstract class Product {
    private static int counter = 0;
    int id;
    String name;
    String productImage;
    int price;
    int discountPrice;
    String sourceURL;

    String brand;
    String color;
    String description;

    int getNewId () {
        return ++counter;
    }
}
