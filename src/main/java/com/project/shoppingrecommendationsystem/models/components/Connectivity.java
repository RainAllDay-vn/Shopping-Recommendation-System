package com.project.shoppingrecommendationsystem.models.components;

public class Connectivity {
    private final String ports;
    private final String wifi;
    private final String bluetooth;
    private final String fingerprint;
    private final String webCam;

    private Connectivity(Builder builder) {
        this.ports = builder.ports;
        this.wifi = builder.wifi;
        this.bluetooth = builder.bluetooth;
        this.fingerprint = builder.fingerprint;
        this.webCam = builder.webCam;
    }

    public String getPorts() {
        return ports;
    }

    public String getWifi() {
        return wifi;
    }

    public String getBluetooth() {
        return bluetooth;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getWebCam() {
        return webCam;
    }

    @Override
    public String toString() {
        return "Connectivity{" +
                "ports='" + ports + '\'' +
                ", wifi='" + wifi + '\'' +
                ", bluetooth='" + bluetooth + '\'' +
                ", fingerprint='" + fingerprint + '\'' +
                ", webCam='" + webCam + '\'' +
                '}';
    }

    public static class Builder {
        private String ports;
        private String wifi;
        private String bluetooth;
        private String fingerprint;
        private String webCam;

        public Builder setPorts(String ports) {
            this.ports = ports;
            return this;
        }

        public Builder setWifi(String wifi) {
            this.wifi = wifi;
            return this;
        }

        public Builder setBluetooth(String bluetooth) {
            this.bluetooth = bluetooth;
            return this;
        }

        public Builder setFingerprint(String fingerprint) {
            this.fingerprint = fingerprint;
            return this;
        }

        public Builder setWebCam(String webCam) {
            this.webCam = webCam;
            return this;
        }

        public Connectivity build() {
            return new Connectivity(this);
        }
    }
}