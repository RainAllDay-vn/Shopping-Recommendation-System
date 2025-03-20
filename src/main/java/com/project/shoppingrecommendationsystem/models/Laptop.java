package com.project.shoppingrecommendationsystem.models;

public class Laptop extends Product {

    public Laptop(int id, String name, String description, String productImage, int price, String sourceURL) {
        super(id, name, description, productImage, price, sourceURL);
    }

    public static Laptop buildTestLaptop() {
        int id = (int)(Math.random()*100);
        String name = "laptop #" + id;
        String description = "laptop #%d's description".formatted(id);
        String productImage = "laptop #%d.jpg".formatted(id);
        int price = (int)(Math.random()*100)*100000;
        String sourceURL = "https://laptop%d".formatted(id);
        return new Laptop(id, name, description, productImage, price, sourceURL);
    }
}
