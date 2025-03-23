package com.project.shoppingrecommendationsystem.models;

public class Laptop extends Product {
    private final CPU cpu;
    private final RAM ram;

    private Laptop(LaptopBuilder builder) {
        super(builder.name, builder.productImage, builder.price, builder.discountPrice, builder.sourceURL, builder.brand,
                builder.color, builder.description);
        this.cpu = builder.cpu;
        this.ram = builder.ram;
    }

    public CPU getCPU() {
        return cpu;
    }

    public RAM getRam() {
        return ram;
    }

    // Builder: LaptopBuilder
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

        public Laptop build() {
            return new Laptop(this);
        }
    }
}
