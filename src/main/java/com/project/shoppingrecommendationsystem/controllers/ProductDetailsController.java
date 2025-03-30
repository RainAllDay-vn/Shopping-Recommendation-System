package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.views.ProductCard;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import javafx.scene.control.Label;

public class ProductDetailsController{
    @FXML private Label  descriptionText;
    @FXML private VBox productCardDetails;

    @FXML private Label batteryCapacityText;
    @FXML private Label batteryFullChargingTimeText;
    @FXML private Label batteryChargePowerText;

    @FXML private Label connectivityPortsText;
    @FXML private Label connectivityWifiText;
    @FXML private Label connectivityBluetoothText;
    @FXML private Label connectivityFingerprintText;
    @FXML private Label connectivityWebcamText;

    @FXML private Label CPUNameText;
    @FXML private Label CPUBaseFrequencyText;
    @FXML private Label CPUTurboFrequencyText;
    @FXML private Label CPUCoresText;
    @FXML private Label CPUThreadsText;
    @FXML private Label CPUCacheText;

    @FXML private Label displayGPUNameText;
    @FXML private Label displayGPUTypeText;
    @FXML private Label displayGPUBaseClockText;
    @FXML private Label displayGPUBoostClockText;
    @FXML private Label displayScreenSizeText;
    @FXML private Label displayScreenResolutionText;
    @FXML private Label displayRefreshRateText;

    @FXML private Label laptopCaseWeightText;
    @FXML private Label laptopCaseDimensionsText;
    @FXML private Label laptopCaseMaterialText;

    @FXML private Label RAMSizeText;
    @FXML private Label RAMChannelsText;
    @FXML private Label RAMClockText;
    @FXML private Label RAMTypeText;
    @FXML private Label RAMUpgradableText;
    @FXML private Label RAMSlotsText;
    @FXML private Label RAMMaxSizeText;

    @FXML private Label storageSizeText;
    @FXML private Label storageBusText;
    @FXML private Label storageTypeText;
    @FXML private Label storageChannelsText;
    @FXML private Label storageUpgradableText;
    @FXML private Label storageSlotsText;



    public void setProductDetails(Laptop product){
        productCardDetails.getChildren().add(new ProductCard(product).getRoot());
        descriptionText.setText(product.getDescription());

        batteryCapacityText.setText(product.getBattery().getCapacity());
        batteryFullChargingTimeText.setText(product.getBattery().getFullyChargingTime());
        batteryChargePowerText.setText(product.getBattery().getChargePower());

        connectivityPortsText.setText(product.getConnectivity().getPorts());
        connectivityWifiText.setText(product.getConnectivity().getWifi());
        connectivityBluetoothText.setText(product.getConnectivity().getBluetooth());
        connectivityFingerprintText.setText(product.getConnectivity().getFingerprint());
        connectivityWebcamText.setText(product.getConnectivity().getWebCam());

        CPUNameText.setText(product.getCpu().getName());
        CPUBaseFrequencyText.setText(product.getCpu().getBaseFrequency());
        CPUTurboFrequencyText.setText(product.getCpu().getTurboFrequency());
        CPUCoresText.setText(product.getCpu().getCores());
        CPUThreadsText.setText(product.getCpu().getThreads());
        CPUCacheText.setText(product.getCpu().getCache());

        displayGPUNameText.setText(product.getDisplay().getGpuName());
        displayGPUTypeText.setText(product.getDisplay().getGpuType());
        displayGPUBaseClockText.setText(product.getDisplay().getGpuBaseClock());
        displayGPUBoostClockText.setText(product.getDisplay().getGpuBoostClock());
        displayScreenSizeText.setText(product.getDisplay().getScreenSize());
        displayScreenResolutionText.setText(product.getDisplay().getScreenResolution());
        displayRefreshRateText.setText(product.getDisplay().getRefreshRate());

        laptopCaseWeightText.setText(product.getLaptopCase().getWeight());
        laptopCaseDimensionsText.setText(product.getLaptopCase().getDimensions());
        laptopCaseMaterialText.setText(product.getLaptopCase().getMaterial());

        RAMSizeText.setText(product.getRam().getSize());
        RAMChannelsText.setText(product.getRam().getChannels());
        RAMClockText.setText(product.getRam().getClock());
        RAMTypeText.setText(product.getRam().getType());
        RAMUpgradableText.setText(product.getRam().getUpgradable());
        RAMSlotsText.setText(product.getRam().getSlots());
        RAMMaxSizeText.setText(product.getRam().getMaxSize());

        storageSizeText.setText(product.getStorage().getSize());
        storageBusText.setText(product.getStorage().getBus());
        storageTypeText.setText(product.getStorage().getStorageType());
        storageChannelsText.setText(product.getStorage().getChannels());
        storageUpgradableText.setText(product.getStorage().getUpgradable());
        storageSlotsText.setText(product.getStorage().getSlots());
    }
}
