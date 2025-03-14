package com.project.shoppingrecommendationsystem.models;

public class Image {
    private Product product;
    private String URL;

    public Image(Product product, String URL) {
        this.product = product;
        this.URL = URL;
    }

    public String getURL() {
        return URL;
    }
}
