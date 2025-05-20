package com.project.shoppingrecommendationsystem.models.components;

import com.opencsv.bean.CsvBindByName;

public class Storage {
    private final String size;
    private final String bus;
    private final String storageType;
    private final String channels;
    private final String upgradable;
    private final String slots;

    private Storage (StorageBuilder builder) {
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

    public static class StorageBuilder {
        private String size;
        private String bus;
        private String storageType;
        private String channels;
        private String upgradable;
        private String slots;

        public StorageBuilder setSize(String size) {
            this.size = size;
            return this;
        }

        public StorageBuilder setBus(String bus) {
            this.bus = bus;
            return this;
        }

        public StorageBuilder setStorageType(String storageType) {
            this.storageType = storageType;
            return this;
        }

        public StorageBuilder setChannels(String channels) {
            this.channels = channels;
            return this;
        }

        public StorageBuilder setUpgradable(String upgradable) {
            this.upgradable = upgradable;
            return this;
        }

        public StorageBuilder setSlots(String slots) {
            this.slots = slots;
            return this;
        }

        public Storage build() {
            return new Storage(this);
        }
    }
}
