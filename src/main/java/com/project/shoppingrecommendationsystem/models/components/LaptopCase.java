package com.project.shoppingrecommendationsystem.models.components;

import com.opencsv.bean.CsvBindByName;

public class LaptopCase {
    @CsvBindByName(column = "LAPTOPCASE-WEIGHT")
    private final String weight;
    @CsvBindByName(column = "LAPTOPCASE-DIMENSIONS")
    private final String dimensions;
    @CsvBindByName(column = "LAPTOPCASE-MATERIAL")
    private final String material;

    private LaptopCase(LaptopCaseBuilder builder) {
        this.weight = builder.weight;
        this.dimensions = builder.dimensions;
        this.material = builder.material;
    }

    public String getWeight() {
        return weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public String getMaterial() {
        return material;
    }

    @Override
    public String toString() {
        return "LaptopCase{" +
                "weight='" + weight + '\'' +
                ", dimensions='" + dimensions + '\'' +
                ", material='" + material + '\'' +
                '}';
    }

    public static class LaptopCaseBuilder {
        private String weight;
        private String dimensions;
        private String material;

        public LaptopCaseBuilder setWeight(String weight) {
            this.weight = weight;
            return this;
        }

        public LaptopCaseBuilder setDimensions(String dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public LaptopCaseBuilder setMaterial(String material) {
            this.material = material;
            return this;
        }

        public LaptopCase build() {
            return new LaptopCase(this);
        }
    }
}