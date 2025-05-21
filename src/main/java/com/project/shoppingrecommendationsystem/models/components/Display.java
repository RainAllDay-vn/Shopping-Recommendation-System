package com.project.shoppingrecommendationsystem.models.components;

import com.opencsv.bean.CsvBindByName;

public class Display {
    private final String gpuName;
    private final String gpuType;
    private final String gpuBaseClock;
    private final String gpuBoostClock;
    private final String screenSize;
    private final String screenResolution;
    private final String refreshRate;

    private Display(DisplayBuilder builder) {
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

    public static class DisplayBuilder {
        private String gpuName;
        private String gpuType;
        private String gpuBaseClock;
        private String gpuBoostClock;
        private String screenSize;
        private String screenResolution;
        private String refreshRate;

        public DisplayBuilder setGpuName(String gpuName) {
            this.gpuName = gpuName;
            return this;
        }

        public DisplayBuilder setGpuType(String gpuType) {
            this.gpuType = gpuType;
            return this;
        }

        public DisplayBuilder setGpuBaseClock(String gpuBaseClock) {
            this.gpuBaseClock = gpuBaseClock;
            return this;
        }

        public DisplayBuilder setGpuBoostClock(String gpuBoostClock) {
            this.gpuBoostClock = gpuBoostClock;
            return this;
        }

        public DisplayBuilder setScreenSize(String screenSize) {
            this.screenSize = screenSize;
            return this;
        }

        public DisplayBuilder setScreenResolution(String screenResolution) {
            this.screenResolution = screenResolution;
            return this;
        }

        public DisplayBuilder setRefreshRate(String refreshRate) {
            this.refreshRate = refreshRate;
            return this;
        }

        public Display build() {
            return new Display(this);
        }
    }
}
