package com.project.shoppingrecommendationsystem.models.components;

public class Battery {
    private final String capacity;
    private final String fullyChargingTime;
    private final String chargePower;

    private Battery(BatteryBuilder builder) {
        this.capacity = builder.capacity;
        this.fullyChargingTime = builder.fullyChargingTime;
        this.chargePower = builder.chargePower;
    }

    public String getCapacity() {
        return capacity;
    }

    public String getFullyChargingTime() {
        return fullyChargingTime;
    }

    public String getChargePower() {
        return chargePower;
    }

    @Override
    public String toString() {
        return "Battery{" +
                "capacity='" + capacity + '\'' +
                ", fullyChargingTime='" + fullyChargingTime + '\'' +
                ", chargePower='" + chargePower + '\'' +
                '}';
    }

    public static class BatteryBuilder {
        private String capacity;
        private String fullyChargingTime;
        private String chargePower;

        public BatteryBuilder setCapacity(String capacity) {
            this.capacity = capacity;
            return this;
        }

        public BatteryBuilder setFullyChargingTime(String fullyChargingTime) {
            this.fullyChargingTime = fullyChargingTime;
            return this;
        }

        public BatteryBuilder setChargePower(String chargePower) {
            this.chargePower = chargePower;
            return this;
        }

        public Battery build() {
            return new Battery(this);
        }
    }
}