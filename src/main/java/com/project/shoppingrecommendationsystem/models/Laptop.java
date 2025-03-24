package com.project.shoppingrecommendationsystem.models;

import com.project.shoppingrecommendationsystem.models.components.*;

public class Laptop extends Product {
    private final CPU cpu;
    private final RAM ram;
    private final Storage storage;
    private final Connectivity connectivity;
    private final Battery battery;
    private final LaptopCase laptopCase;

    private Laptop(LaptopBuilder builder) {
        super(builder.name, builder.productImage, builder.price, builder.discountPrice, builder.sourceURL, builder.brand,
                builder.color, builder.description);
        this.cpu = builder.cpu;
        this.ram = builder.ram;
        this.storage = builder.storage;
        this.connectivity = builder.connectivity;
        this.battery = builder.battery;
        this.laptopCase = builder.laptopCase;
    }

    public CPU getCPU() {
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
                "    name='" + name + '\'' + "\n" +
                "    productImage='" + productImage + '\'' + "\n" +
                "    price=" + price + "\n" +
                "    discountPrice=" + discountPrice + "\n" +
                "    sourceURL='" + sourceURL + '\'' + "\n" +
                "    brand='" + brand + '\'' + "\n" +
                "    color='" + color + '\'' + "\n" +
                "    description='" + description + '\'' + "\n" +
                '}';
    }

    public boolean match(String[][] query) {
        try {
            for (String[] field : query) {
                field[0] = switch (field[0]) {
                    case "name" -> name;
                    case "brand" -> brand;
                    case "price" -> String.valueOf(discountPrice);
                    default -> "false";
                };
                if (field[0].equals("false") || !matchField(field)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Invalid query");
            return false;
        }
    }

    private boolean matchField (String[] fields) {
        try {
            return switch (fields[1].toLowerCase()) {
                case "contain" -> fields[2].toLowerCase().contains(fields[0].toLowerCase());
                case "in" -> {
                    for (int i=2; i<fields.length; i++) {
                        if (fields[i].equalsIgnoreCase(fields[0])) {
                            yield true;
                        }
                    }
                    yield false;
                }
                case "between" -> {
                    int lower = Integer.parseInt(fields[2]);
                    int upper = Integer.parseInt(fields[3]);
                    int value = Integer.parseInt(fields[0]);
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

        public Laptop build() {
            return new Laptop(this);
        }
    }
}