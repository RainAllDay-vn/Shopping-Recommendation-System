package com.project.shoppingrecommendationsystem.models;

import com.project.shoppingrecommendationsystem.models.components.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Laptop extends Product {
    private final CPU cpu;
    private final RAM ram;
    private final Storage storage;
    private final Connectivity connectivity;
    private final Battery battery;
    private final LaptopCase laptopCase;
    private final Display display;

    private Laptop(LaptopBuilder builder) {
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

    public LaptopCase getLaptopCase() {
        return laptopCase;
    }

    public Display getDisplay() {
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

    public boolean match(List<String[]> query) {
        try {
            for (String[] field : query) {
                String[] copy = Arrays.copyOf(field, field.length);
                copy[0] = switch (copy[0]) {
                    case "name" -> name;
                    case "brand" -> brand;
                    case "price" -> String.valueOf(discountPrice);
                    case "description" -> description;
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

    private boolean matchField (String[] field) {
        try {
            return switch (field[1].toLowerCase()) {
                case "contain" -> field[0].toLowerCase().contains(field[2].toLowerCase());
                case "in" -> {
                    for (int i=2; i<field.length; i++) {
                        if (field[i].equalsIgnoreCase(field[0])) {
                            yield true;
                        }
                    }
                    yield false;
                }
                case "between" -> {
                    int lower = field[2].isEmpty() ? 0 : Integer.parseInt(field[2]);
                    int upper = field[3].isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(field[3]);
                    int value = Integer.parseInt(field[0]);
                    yield lower <= value && value <= upper;
                }
                default -> false;
            };
        } catch (Exception e) {
            System.err.println("Invalid query");
            return false;
        }
    }

    public static class LaptopBuilder {
        private String name;
        private String productImage;
        private int price;
        private int discountPrice;
        private List<Review> reviews = new ArrayList<>();
        private String source;
        private String sourceURL;

        private String brand;
        private String color;
        private String description;

        private CPU cpu;
        private RAM ram;
        private Storage storage;
        private Connectivity connectivity;
        private Battery battery;
        private LaptopCase laptopCase;
        private Display display;

        public LaptopBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public LaptopBuilder setProductImage(String productImage) {
            this.productImage = productImage;
            return this;
        }

        public LaptopBuilder setPrice(int price) {
            this.price = price;
            return this;
        }

        public LaptopBuilder setDiscountPrice(int discountPrice) {
            this.discountPrice = discountPrice;
            return this;
        }

        public LaptopBuilder addReview(Review review) {
            this.reviews.add(review);
            return this;
        }

        public LaptopBuilder setSource(String source) {
            this.source = source;
            return this;
        }

        public LaptopBuilder setSourceURL(String sourceURL) {
            this.sourceURL = sourceURL;
            return this;
        }

        public LaptopBuilder setBrand(String brand) {
            this.brand = brand;
            return this;
        }

        public LaptopBuilder setColor(String color) {
            this.color = color;
            return this;
        }

        public LaptopBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public LaptopBuilder setCpu(CPU cpu) {
            this.cpu = cpu;
            return this;
        }

        public LaptopBuilder setRam(RAM ram) {
            this.ram = ram;
            return this;
        }

        public LaptopBuilder setStorage(Storage storage) {
            this.storage = storage;
            return this;
        }

        public LaptopBuilder setConnectivity(Connectivity connectivity) {
            this.connectivity = connectivity;
            return this;
        }

        public LaptopBuilder setBattery(Battery battery) {
            this.battery = battery;
            return this;
        }

        public LaptopBuilder setLaptopCase(LaptopCase laptopCase) {
            this.laptopCase = laptopCase;
            return this;
        }

        public LaptopBuilder setDisplay(Display display) {
            this.display = display;
            return this;
        }

        public Laptop build() {
            return new Laptop(this);
        }
    }
}