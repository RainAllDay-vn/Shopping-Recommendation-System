package com.project.shoppingrecommendationsystem.models.components;

public class Storage {
    private final String size;
    private final String bus;
    private final String storageType;
    private final String channels;
    private final String upgradable;
    private final String slots;

    private Storage (Builder builder) {
        this.size = builder.size;
        this.bus = builder.bus;
        this.storageType = builder.storageType;
        this.channels = builder.channels;
        this.upgradable = builder.upgradable;
        this.slots = builder.slots;
    }

    public String getSize() {
        return size;
    }

    public String getBus() {
        return bus;
    }

    public String getStorageType() {
        return storageType;
    }

    public String getChannels() {
        return channels;
    }

    public String getUpgradable() {
        return upgradable;
    }

    public String getSlots() {
        return slots;
    }

    @Override
    public String toString() {
        return "Storage{" +
                "size='" + size + '\'' +
                ", bus='" + bus + '\'' +
                ", storageType='" + storageType + '\'' +
                ", channels='" + channels + '\'' +
                ", upgradable='" + upgradable + '\'' +
                ", slots='" + slots + '\'' +
                '}';
    }

    public static class Builder {
        private String size;
        private String bus;
        private String storageType;
        private String channels;
        private String upgradable;
        private String slots;

        public Builder setSize(String size) {
            this.size = size;
            return this;
        }

        public Builder setBus(String bus) {
            this.bus = bus;
            return this;
        }

        public Builder setStorageType(String storageType) {
            this.storageType = storageType;
            return this;
        }

        public Builder setChannels(String channels) {
            this.channels = channels;
            return this;
        }

        public Builder setUpgradable(String upgradable) {
            this.upgradable = upgradable;
            return this;
        }

        public Builder setSlots(String slots) {
            this.slots = slots;
            return this;
        }

        public Storage build() {
            return new Storage(this);
        }
    }
}
