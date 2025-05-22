package com.project.shoppingrecommendationsystem.models;

import com.project.shoppingrecommendationsystem.models.components.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SmartPhone extends Product {
    private final CPU cpu;
    private final RAM ram;
    private final Storage storage;
    private final Connectivity connectivity;
    private final Camera camera;
    private final Battery battery;
    private final Case phoneCase;
    private final PhoneDisplay display;

    private SmartPhone(Builder builder) {
        super(builder.name, builder.productImage, builder.price, builder.discountPrice, builder.reviews, builder.source, builder.sourceURL,
                builder.brand, builder.color, builder.description);
        this.cpu = builder.cpu;
        this.ram = builder.ram;
        this.storage = builder.storage;
        this.connectivity = builder.connectivity;
        this.camera = builder.camera;
        this.battery = builder.battery;
        this.phoneCase = builder.phoneCase;
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

    public Camera getCamera() {
        return camera;
    }

    public Battery getBattery() {
        return battery;
    }

    public Case getPhoneCase() {
        return phoneCase;
    }

    public PhoneDisplay getDisplay() {
        return display;
    }

    @Override
    public String toString() {
        return "SmartPhone{" + "\n" +
                "    id=" + id + "\n" +
                "    cpu=" + cpu + "\n" +
                "    ram=" + ram + "\n" +
                "    storage=" + storage + "\n" +
                "    connectivity=" + connectivity + "\n" +
                "    camera=" + camera + "\n" +
                "    battery=" + battery + "\n" +
                "    phoneCase=" + phoneCase + "\n" +
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

    @Override
    public boolean match(List<String[]> query) {
        try {
            for (String[] field : query) {
                String[] copy = Arrays.copyOf(field, field.length);
                copy[0] = switch (copy[0]) {
                    case "name" -> name;
                    case "brand" -> brand;
                    case "price" -> String.valueOf(discountPrice);
                    case "description" -> {
                        StringBuilder compactedDescription = new StringBuilder();
                        for (String[] paragraph : description) {
                            compactedDescription.append(paragraph[0]);
                            compactedDescription.append(" ");
                        }
                        yield compactedDescription.toString();
                    }
                    default -> "false";
                };
                if (copy[0].equals("false") || !matchField(copy)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Invalid query");
            return false;
        }
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
        private Camera camera;
        private Battery battery;
        private Case phoneCase;
        private PhoneDisplay display;

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

        public Builder addReview(Review review) {
            this.reviews.add(review);
            return this;
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

        public Builder setCamera(Camera camera) {
            this.camera = camera;
            return this;
        }

        public Builder setBattery(Battery battery) {
            this.battery = battery;
            return this;
        }

        public Builder setPhoneCase(Case phoneCase) {
            this.phoneCase = phoneCase;
            return this;
        }

        public Builder setDisplay(PhoneDisplay display) {
            this.display = display;
            return this;
        }

        public SmartPhone build() {
            return new SmartPhone(this);
        }
    }
}
