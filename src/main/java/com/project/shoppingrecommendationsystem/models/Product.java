package com.project.shoppingrecommendationsystem.models;

import java.util.Map;

import org.openqa.selenium.json.Json;
import org.openqa.selenium.json.JsonInput;

public abstract class Product {
    private int id;
    private String name;
    private String description;
    private int price;
    private String sourceURL;
    private String imageURL;
    private Map<String,String> attributes;
    public Product(int id, String name, String description, int price, String sourceURL, String imageURL,Map<String,String> attributes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.sourceURL = sourceURL;
        this.imageURL = imageURL;
        this.attributes = attributes;
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

    public int getPrice() {
        return price;
    }


    public String getSourceURL() {
        return sourceURL;
    }
    public String getImageURL(){
        return this.imageURL;
    }
    public String getAttribute(String attributeName){
        return attributes.get(attributeName);
    }
    public String getAttributeAsJSon(){
        return new Json().toJson(attributes);
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
