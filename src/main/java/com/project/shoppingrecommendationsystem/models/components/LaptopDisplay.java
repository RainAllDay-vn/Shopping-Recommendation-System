package com.project.shoppingrecommendationsystem.models.components;

public class LaptopDisplay {
    private final String gpuName;
    private final String gpuType;
    private final String gpuBaseClock;
    private final String gpuBoostClock;
    private final String screenSize;
    private final String screenResolution;
    private final String refreshRate;

    private LaptopDisplay(Builder builder) {
        this.gpuName = builder.gpuName;
        this.gpuType = builder.gpuType;
        this.gpuBaseClock = builder.gpuBaseClock;
        this.gpuBoostClock = builder.gpuBoostClock;
        this.screenSize = builder.screenSize;
        this.screenResolution = builder.screenResolution;
        this.refreshRate = builder.refreshRate;
    }

    public String getGpuName() {
        return gpuName;
    }

    public String getGpuType() {
        return gpuType;
    }

    public String getGpuBaseClock() {
        return gpuBaseClock;
    }

    public String getGpuBoostClock() {
        return gpuBoostClock;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public String getScreenResolution() {
        return screenResolution;
    }

    public String getRefreshRate() {
        return refreshRate;
    }

    @Override
    public String toString() {
        return "Display{" +
                "gpuName='" + gpuName + '\'' +
                ", gpuType='" + gpuType + '\'' +
                ", gpuBaseClock='" + gpuBaseClock + '\'' +
                ", gpuBoostClock='" + gpuBoostClock + '\'' +
                ", screenSize='" + screenSize + '\'' +
                ", screenResolution='" + screenResolution + '\'' +
                ", refreshRate='" + refreshRate + '\'' +
                '}';
    }

    public static class Builder {
        private String gpuName;
        private String gpuType;
        private String gpuBaseClock;
        private String gpuBoostClock;
        private String screenSize;
        private String screenResolution;
        private String refreshRate;

        public Builder setGpuName(String gpuName) {
            this.gpuName = gpuName;
            return this;
        }

        public Builder setGpuType(String gpuType) {
            this.gpuType = gpuType;
            return this;
        }

        public Builder setGpuBaseClock(String gpuBaseClock) {
            this.gpuBaseClock = gpuBaseClock;
            return this;
        }

        public Builder setGpuBoostClock(String gpuBoostClock) {
            this.gpuBoostClock = gpuBoostClock;
            return this;
        }

        public Builder setScreenSize(String screenSize) {
            this.screenSize = screenSize;
            return this;
        }

        public Builder setScreenResolution(String screenResolution) {
            this.screenResolution = screenResolution;
            return this;
        }

        public Builder setRefreshRate(String refreshRate) {
            this.refreshRate = refreshRate;
            return this;
        }

        public LaptopDisplay build() {
            return new LaptopDisplay(this);
        }
    }
}
