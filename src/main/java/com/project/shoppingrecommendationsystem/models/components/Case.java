package com.project.shoppingrecommendationsystem.models.components;

public class Case {
    private final String weight;
    private final String dimensions;
    private final String material;

    private Case(Builder builder) {
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

    public static class Builder {
        private String weight;
        private String dimensions;
        private String material;

        public Builder setWeight(String weight) {
            this.weight = weight;
            return this;
        }

        public Builder setDimensions(String dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public Builder setMaterial(String material) {
            this.material = material;
            return this;
        }

        public Case build() {
            return new Case(this);
        }
    }
}