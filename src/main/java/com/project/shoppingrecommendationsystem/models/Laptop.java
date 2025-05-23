package com.project.shoppingrecommendationsystem.models;

import com.project.shoppingrecommendationsystem.models.components.*;

import java.util.ArrayList;
import java.util.List;

public class Laptop extends Product {
    private final CPU cpu;
    private final RAM ram;
    private final Storage storage;
    private final Connectivity connectivity;
    private final Battery battery;
    private final Case laptopCase;
    private final LaptopDisplay display;

    private Laptop(Builder builder) {
        super(builder.name, builder.productImage, builder.price, builder.discountPrice, builder.reviews, builder.source, builder.sourceURL,
                builder.brand, builder.color, builder.description);
        this.cpu = builder.cpu;
        this.ram = builder.ram;
        this.storage = builder.storage;
        this.connectivity = builder.connectivity;
        this.battery = builder.battery;
        this.laptopCase = builder.laptopCase;
        this.display = builder.display;
    }

    public CPU getCpu() {
        return cpu;
    }

    public RAM getRam() {
        return ram;
    }

    public Storage getStorage() {
        return storage;
    }

    public Connectivity getConnectivity() {
        return connectivity;
    }

    public Battery getBattery() {
        return battery;
    }

    public Case getLaptopCase() {
        return laptopCase;
    }

    public LaptopDisplay getDisplay() {
        return display;
    }

    @Override
    public String toString() {
        return "Laptop{" + "\n" +
                "    id=" + id + "\n" +
                "    cpu=" + cpu + "\n" +
                "    ram=" + ram + "\n" +
                "    storage=" + storage + "\n" +
                "    connectivity=" + connectivity + "\n" +
                "    battery=" + battery + "\n" +
                "    laptopCase=" + laptopCase + "\n" +
                "    display=" + display + "\n" +
                "    name='" + name + '\'' + "\n" +
                "    productImage='" + productImage + '\'' + "\n" +
                "    price=" + price + "\n" +
                "    discountPrice=" + discountPrice + "\n" +
                "    reviews=" + reviews + "\n" +
                "    sourceURL='" + sourceURL + '\'' + "\n" +
                "    brand='" + brand + '\'' + "\n" +
                "    color='" + color + '\'' + "\n" +
                "    description='" + description + '\'' + "\n" +
                '}';
    }

    public static class Builder {
        private String name;
        private String productImage;
        private int price;
        private int discountPrice;
        private final List<Review> reviews = new ArrayList<>();
        private String source;
        private String sourceURL;

        private String brand;
        private String color;
        private List<String[]> description;

        private CPU cpu;
        private RAM ram;
        private Storage storage;
        private Connectivity connectivity;
        private Battery battery;
        private Case laptopCase;
        private LaptopDisplay display;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setProductImage(String productImage) {
            this.productImage = productImage;
            return this;
        }

        public Builder setPrice(int price) {
            this.price = price;
            return this;
        }

        public Builder setDiscountPrice(int discountPrice) {
            this.discountPrice = discountPrice;
            return this;
        }

        public void addReview(Review review) {
            this.reviews.add(review);
        }

        public Builder setSource(String source) {
            this.source = source;
            return this;
        }

        public Builder setSourceURL(String sourceURL) {
            this.sourceURL = sourceURL;
            return this;
        }

        public Builder setBrand(String brand) {
            this.brand = brand;
            return this;
        }

        public Builder setColor(String color) {
            this.color = color;
            return this;
        }

        public Builder setDescription(List<String[]> description) {
            this.description = description;
            return this;
        }

        public Builder setCpu(CPU cpu) {
            this.cpu = cpu;
            return this;
        }

        public Builder setRam(RAM ram) {
            this.ram = ram;
            return this;
        }

        public Builder setStorage(Storage storage) {
            this.storage = storage;
            return this;
        }

        public Builder setConnectivity(Connectivity connectivity) {
            this.connectivity = connectivity;
            return this;
        }

        public Builder setBattery(Battery battery) {
            this.battery = battery;
            return this;
        }

        public Builder setLaptopCase(Case laptopCase) {
            this.laptopCase = laptopCase;
            return this;
        }

        public Builder setDisplay(LaptopDisplay display) {
            this.display = display;
            return this;
        }

        public Laptop build() {
            return new Laptop(this);
        }
    }
}