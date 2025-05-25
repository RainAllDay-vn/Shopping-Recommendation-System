package com.project.shoppingrecommendationsystem.models.components;

public class PhoneDisplay {

    private final String name;
    private final String size;
    private final String resolution;

    private PhoneDisplay(Builder builder) {
        this.name = builder.name;
        this.size = builder.size;
        this.resolution = builder.resolution;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getResolution() {
        return resolution;
    }

    public static class Builder {
        private String name;
        private String size;
        private String resolution;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setSize(String size) {
            this.size = size;
            return this;
        }

        public Builder setResolution(String resolution) {
            this.resolution = resolution;
            return this;
        }

        public PhoneDisplay build() {
            return new PhoneDisplay(this);
        }
    }

    @Override
    public String toString() {
        return "PhoneDisplay{" +
                "name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", resolution='" + resolution + '\'' +
                '}';
    }
}
