package com.project.shoppingrecommendationsystem.models;

import java.util.LinkedHashMap;

public abstract class Product {

    private final int id;
    private final String name;
    private final String description;
    private final String productImage;
    private final int price;
    private final String sourceURL;
    private final LinkedHashMap<String, String> hardware;

    public Product(int id, String name, String description, String productImage, int price, String sourceURL) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.productImage = productImage;
        this.price = price;
        this.sourceURL = sourceURL;
        this.hardware = new LinkedHashMap<>();
    }

    public void updateHardware(String key, String value) {
        hardware.put(key, value);
    }

    public LinkedHashMap<String, String> getOverview() {
        LinkedHashMap<String, String> overview = new LinkedHashMap<>();
        overview.put("id", String.valueOf(id));
        overview.put("name", name);
        overview.put("description", description);
        overview.put("productImage", productImage);
        overview.put("price", String.valueOf(price));
        overview.put("sourceURL", String.valueOf(sourceURL));
        return overview;
    }

    public LinkedHashMap<String, String> getHardware() {
        return new LinkedHashMap<>(hardware);
    }

    public String getDescription() {
        return description;
    }
}
