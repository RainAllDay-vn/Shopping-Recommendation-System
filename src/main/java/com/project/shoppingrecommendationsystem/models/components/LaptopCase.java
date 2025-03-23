package com.project.shoppingrecommendationsystem.models.components;

public class LaptopCase {
    private final String weight;
    private final String dimensions;
    private final String material;

    private LaptopCase(CaseBuilder builder) {
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

    public static class CaseBuilder {
        private String weight;
        private String dimensions;
        private String material;

        public CaseBuilder setWeight(String weight) {
            this.weight = weight;
            return this;
        }

        public CaseBuilder setDimensions(String dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public CaseBuilder setMaterial(String material) {
            this.material = material;
            return this;
        }

        public LaptopCase build() {
            return new LaptopCase(this);
        }
    }
}