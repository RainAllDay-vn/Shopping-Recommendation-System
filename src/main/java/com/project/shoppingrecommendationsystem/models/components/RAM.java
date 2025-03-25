package com.project.shoppingrecommendationsystem.models.components;

import com.opencsv.bean.CsvBindByName;

public class RAM {
    @CsvBindByName(column = "RAM-SIZE")
    private final String size;
    @CsvBindByName(column = "RAM-CHANNELS")
    private final String channels;
    @CsvBindByName(column = "RAM-CLOCK")
    private final String clock;
    @CsvBindByName(column = "RAM-TYPE")
    private final String type;
    @CsvBindByName(column = "RAM-UPGRADABLE")
    private final String upgradable;
    @CsvBindByName(column = "RAM-SLOTS")
    private final String slots;
    @CsvBindByName(column = "RAM-MAX-SIZE")
    private final String maxSize;

    private RAM(RAMBuilder builder) {
        this.size = builder.size;
        this.channels = builder.channels;
        this.clock = builder.clock;
        this.type = builder.type;
        this.upgradable = builder.upgradable;
        this.slots = builder.slots;
        this.maxSize = builder.maxSize;
    }

    public String getSize() {
        return size;
    }

    public String getChannels() {
        return channels;
    }

    public String getClock() {
        return clock;
    }

    public String getType() {
        return type;
    }

    public String getUpgradable() {
        return upgradable;
    }

    public String getSlots() {
        return slots;
    }

    public String getMaxSize() {
        return maxSize;
    }

    @Override
    public String toString() {
        return "RAM{" +
                "size='" + size + '\'' +
                ", channels='" + channels + '\'' +
                ", clock='" + clock + '\'' +
                ", type='" + type + '\'' +
                ", upgradable='" + upgradable + '\'' +
                ", slots='" + slots + '\'' +
                ", maxSize='" + maxSize + '\'' +
                '}';
    }

    public static class RAMBuilder {
        private String size;
        private String channels;
        private String clock;
        private String type;
        private String upgradable;
        private String slots;
        private String maxSize;

        public RAMBuilder setSize(String size) {
            this.size = size;
            return this;
        }

        public RAMBuilder setChannels(String channels) {
            this.channels = channels;
            return this;
        }

        public RAMBuilder setClock(String clock) {
            this.clock = clock;
            return this;
        }

        public RAMBuilder setType(String type) {
            this.type = type;
            return this;
        }

        public RAMBuilder setUpgradable(String upgradable) {
            this.upgradable = upgradable;
            return this;
        }

        public RAMBuilder setSlots(String slots) {
            this.slots = slots;
            return this;
        }

        public RAMBuilder setMaxSize(String maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public RAM build() {
            return new RAM(this);
        }
    }
}
