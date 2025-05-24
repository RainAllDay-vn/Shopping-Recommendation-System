package com.project.shoppingrecommendationsystem.models.components;

public class CPU {
    private final String name;
    private final String baseFrequency;
    private final String turboFrequency;
    private final String cores;
    private final String threads;
    private final String cache;

    private CPU(Builder builder) {
        this.name = builder.name;
        this.baseFrequency = builder.baseFrequency;
        this.turboFrequency = builder.turboFrequency;
        this.cores = builder.cores;
        this.threads = builder.threads;
        this.cache = builder.cache;
    }

    public String getName() {
        return name;
    }

    public String getBaseFrequency() {
        return baseFrequency;
    }

    public String getTurboFrequency() {
        return turboFrequency;
    }

    public String getCores() {
        return cores;
    }

    public String getThreads() {
        return threads;
    }

    public String getCache() {
        return cache;
    }

    @Override
    public String toString() {
        return "CPU{" +
                "name='" + name + '\'' +
                ", baseFrequency='" + baseFrequency + '\'' +
                ", turboFrequency='" + turboFrequency + '\'' +
                ", cores='" + cores + '\'' +
                ", threads='" + threads + '\'' +
                ", cache='" + cache + '\'' +
                '}';
    }

    public static class Builder {
        private String name;
        private String baseFrequency;
        private String turboFrequency;
        private String cores;
        private String threads;
        private String cache;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setBaseFrequency(String baseFrequency) {
            this.baseFrequency = baseFrequency;
            return this;
        }

        public Builder setTurboFrequency(String turboFrequency) {
            this.turboFrequency = turboFrequency;
            return this;
        }

        public Builder setCores(String cores) {
            this.cores = cores;
            return this;
        }

        public Builder setThreads(String threads) {
            this.threads = threads;
            return this;
        }

        public Builder setCache(String cache) {
            this.cache = cache;
            return this;
        }

        public CPU build() {
            return new CPU(this);
        }
    }
}
