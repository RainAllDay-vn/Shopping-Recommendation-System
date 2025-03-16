package com.project.shoppingrecommendationsystem.models;

import java.util.Map;

public class Laptop extends Product{
    public Laptop(int id, String name, String description, int price, String sourceURL, String imageURL, Map<String,String> attributes) {
        super(id, name, description, price, sourceURL,imageURL, attributes);
    }
}
