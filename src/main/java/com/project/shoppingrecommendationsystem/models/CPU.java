package com.project.shoppingrecommendationsystem.models;

public class CPU {
    private final String name;
    private final String baseFrequency;
    private final String turboFrequency;
    private final String cores;
    private final String threads;
    private final String cache;

    public CPU(CPUBuilder builder) {
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

    public static class CPUBuilder {
        private String name;
        private String baseFrequency;
        private String turboFrequency;
        private String cores;
        private String threads;
        private String cache;

        public CPUBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public CPUBuilder setBaseFrequency(String baseFrequency) {
            this.baseFrequency = baseFrequency;
            return this;
        }

        public CPUBuilder setTurboFrequency(String turboFrequency) {
            this.turboFrequency = turboFrequency;
            return this;
        }

        public CPUBuilder setCores(String cores) {
            this.cores = cores;
            return this;
        }

        public CPUBuilder setThreads(String threads) {
            this.threads = threads;
            return this;
        }

        public CPUBuilder setCache(String cache) {
            this.cache = cache;
            return this;
        }

        public CPU build() {
            return new CPU(this);
        }
    }
}
