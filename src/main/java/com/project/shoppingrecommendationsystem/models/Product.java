package com.project.shoppingrecommendationsystem.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Product {
    private int id;
    private String name;
    private String description;
    private Map<String, String> hardwareMap;
    private List<Image> images;
    private int price;
    private String sourceURL;

    public Product(int id, String name, String description, int price, String sourceURL) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.hardwareMap = new LinkedHashMap<>();
        this.images = new ArrayList<>();
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

    public Map<String, String> getHardwareMap() {
        return hardwareMap;
    }

    public List<Image> getImages() {
        return images;
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

    public void addHardware(String key, String value) {
        hardwareMap.put(key, value);
    }

    public void addImage(String imageURL) {
        this.images.add(new Image(this, imageURL));
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
