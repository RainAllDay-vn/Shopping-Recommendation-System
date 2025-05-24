package com.project.shoppingrecommendationsystem.models.components;

public class Camera {

    private final String frontCameraName;
    private final String frontCameraResolution;
    private final String backCameraName;
    private final String backCameraResolution;

    private Camera(Builder builder) {
        this.frontCameraName = builder.frontCameraName;
        this.frontCameraResolution = builder.frontCameraResolution;
        this.backCameraName = builder.backCameraName;
        this.backCameraResolution = builder.backCameraResolution;
    }

    public String getFrontCameraName() {
        return frontCameraName;
    }

    public String getFrontCameraResolution() {
        return frontCameraResolution;
    }

    public String getBackCameraName() {
        return backCameraName;
    }

    public String getBackCameraResolution() {
        return backCameraResolution;
    }

    public static class Builder {
        private String frontCameraName;
        private String frontCameraResolution;
        private String backCameraName;
        private String backCameraResolution;

        public Builder setFrontCameraName(String frontCameraName) {
            this.frontCameraName = frontCameraName;
            return this;
        }

        public Builder setFrontCameraResolution(String frontCameraResolution) {
            this.frontCameraResolution = frontCameraResolution;
            return this;
        }

        public Builder setBackCameraName(String backCameraName) {
            this.backCameraName = backCameraName;
            return this;
        }

        public Builder setBackCameraResolution(String backCameraResolution) {
            this.backCameraResolution = backCameraResolution;
            return this;
        }

        public Camera build() {
            return new Camera(this);
        }
    }

    @Override
    public String toString() {
        return "Camera{" +
                "frontCameraName='" + frontCameraName + '\'' +
                ", frontCameraResolution='" + frontCameraResolution + '\'' +
                ", backCameraName='" + backCameraName + '\'' +
                ", backCameraResolution='" + backCameraResolution + '\'' +
                '}';
    }

    public static void main(String[] args) {
        Camera camera = new Camera.Builder()
                .setFrontCameraName("Wide Angle")
                .setFrontCameraResolution("12MP")
                .setBackCameraName("Main")
                .setBackCameraResolution("48MP")
                .build();

        System.out.println(camera);
    }
}
