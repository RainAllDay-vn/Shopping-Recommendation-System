package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.components.Review;
import com.project.shoppingrecommendationsystem.models.components.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class LaptopDetailsController {

    @FXML
    private VBox descriptionBox;
    @FXML
    private VBox productPage;
    @FXML
    private VBox productCardDetailsBox;
    @FXML
    private HBox cpuNameRow, cpuBaseFrequencyRow, cpuTurboFrequencyRow, cpuCoresRow, cpuThreadsRow, cpuCacheRow;
    @FXML
    private Label cpuNameText, cpuBaseFrequencyText, cpuTurboFrequencyText, cpuCoresText, cpuThreadsText, cpuCacheText;
    @FXML
    private HBox ramSizeRow, ramTypeRow, ramChannelsRow, ramClockRow, ramUpgradableRow, ramSlotsRow, ramMaxSizeRow;
    @FXML
    private Label ramSizeText, ramTypeText, ramChannelsText, ramClockText, ramUpgradableText, ramSlotsText, ramMaxSizeText;
    @FXML
    private HBox storageSizeRow, storageBusRow, storageTypeRow, storageChannelsRow, storageUpgradableRow, storageSlotsRow;
    @FXML
    private Label storageSizeText, storageBusText, storageTypeText, storageChannelsText, storageUpgradableText, storageSlotsText;
    @FXML
    private HBox displayScreenSizeRow, displayScreenResolutionRow, displayRefreshRateRow, displayGPUNameRow, displayGPUTypeRow, displayGPUBaseClockRow, displayGPUBoostClockRow;
    @FXML
    private Label displayScreenSizeText, displayScreenResolutionText, displayRefreshRateText, displayGPUNameText, displayGPUTypeText, displayGPUBaseClockText, displayGPUBoostClockText;
    @FXML
    private HBox connectivityPortsRow, connectivityWifiRow, connectivityBluetoothRow, connectivityFingerprintRow, connectivityWebcamRow;
    @FXML
    private Label connectivityPortsText, connectivityWifiText, connectivityBluetoothText, connectivityFingerprintText, connectivityWebcamText;
    @FXML
    private HBox batteryCapacityRow, batteryFullChargingTimeRow, batteryChargePowerRow;
    @FXML
    private Label batteryCapacityText, batteryFullChargingTimeText, batteryChargePowerText;
    @FXML
    private HBox laptopCaseWeightRow, laptopCaseDimensionsRow, laptopCaseMaterialRow;
    @FXML
    private Label laptopCaseWeightText, laptopCaseDimensionsText, laptopCaseMaterialText;

    private void setLabelText(Label label, String value, HBox row) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {
            hideRow(row);
        } else {
            value = value.replaceAll("<br\\s*/?>", "");
            value = value.replaceAll("\"", "");
            label.setText(value);
            label.setVisible(true);
            label.setManaged(true);
            if (row != null) {
                row.setVisible(true);
                row.setManaged(true);
            }
        }
    }

    private void hideRow(HBox row) {
        if (row != null) {
            row.setVisible(false);
            row.setManaged(false);
        }
    }

    public void setProductDetails(Laptop product, Node productCard) {
        productCardDetailsBox.getChildren().add(productCard);

        displayProductDescription(product.getDescription());

        setCPUDetails(product.getCpu());
        setRAMDetails(product.getRam());
        setStorageDetails(product.getStorage());
        setDisplayDetails(product.getDisplay());
        setConnectivityDetails(product.getConnectivity());
        setBatteryDetails(product.getBattery());
        setLaptopCaseDetails(product.getLaptopCase());
        displayProductReview(product.getReviews());
    }

    private void setCPUDetails(CPU cpu) {
        setLabelText(cpuNameText, cpu.getName(), cpuNameRow);
        setLabelText(cpuBaseFrequencyText, cpu.getBaseFrequency(), cpuBaseFrequencyRow);
        setLabelText(cpuTurboFrequencyText, cpu.getTurboFrequency(), cpuTurboFrequencyRow);
        setLabelText(cpuCoresText, cpu.getCores(), cpuCoresRow);
        setLabelText(cpuThreadsText, cpu.getThreads(), cpuThreadsRow);
        setLabelText(cpuCacheText, cpu.getCache(), cpuCacheRow);
    }

    private void setRAMDetails(RAM ram) {
        setLabelText(ramSizeText, ram.getSize(), ramSizeRow);
        setLabelText(ramTypeText, ram.getType(), ramTypeRow);
        setLabelText(ramChannelsText, ram.getChannels(), ramChannelsRow);
        setLabelText(ramClockText, ram.getClock(), ramClockRow);
        setLabelText(ramUpgradableText, ram.getUpgradable(), ramUpgradableRow);
        setLabelText(ramSlotsText, ram.getSlots(), ramSlotsRow);
        setLabelText(ramMaxSizeText, ram.getMaxSize(), ramMaxSizeRow);
    }

    private void setStorageDetails(Storage storage) {
        setLabelText(storageSizeText, storage.getSize(), storageSizeRow);
        setLabelText(storageBusText, storage.getBus(), storageBusRow);
        setLabelText(storageTypeText, storage.getStorageType(), storageTypeRow);
        setLabelText(storageChannelsText, storage.getChannels(), storageChannelsRow);
        setLabelText(storageUpgradableText, storage.getUpgradable(), storageUpgradableRow);
        setLabelText(storageSlotsText, storage.getSlots(), storageSlotsRow);
    }

    private void setDisplayDetails(LaptopDisplay display) {
        setLabelText(displayScreenSizeText, display.getScreenSize(), displayScreenSizeRow);
        setLabelText(displayScreenResolutionText, display.getScreenResolution(), displayScreenResolutionRow);
        setLabelText(displayRefreshRateText, display.getRefreshRate(), displayRefreshRateRow);
        setLabelText(displayGPUNameText, display.getGpuName(), displayGPUNameRow);
        setLabelText(displayGPUTypeText, display.getGpuType(), displayGPUTypeRow);
        setLabelText(displayGPUBaseClockText, display.getGpuBaseClock(), displayGPUBaseClockRow);
        setLabelText(displayGPUBoostClockText, display.getGpuBoostClock(), displayGPUBoostClockRow);
    }

    private void setConnectivityDetails(Connectivity connectivity) {
        setLabelText(connectivityPortsText, connectivity.getPorts(), connectivityPortsRow);
        setLabelText(connectivityWifiText, connectivity.getWifi(), connectivityWifiRow);
        setLabelText(connectivityBluetoothText, connectivity.getBluetooth(), connectivityBluetoothRow);
        setLabelText(connectivityFingerprintText, connectivity.getFingerprint(), connectivityFingerprintRow);
        setLabelText(connectivityWebcamText, connectivity.getWebCam(), connectivityWebcamRow);
    }

    private void setBatteryDetails(Battery battery) {
        setLabelText(batteryCapacityText, battery.getCapacity(), batteryCapacityRow);
        setLabelText(batteryFullChargingTimeText, battery.getFullyChargingTime(), batteryFullChargingTimeRow);
        setLabelText(batteryChargePowerText, battery.getChargePower(), batteryChargePowerRow);
    }

    private void setLaptopCaseDetails(Case laptopCase) {
        setLabelText(laptopCaseWeightText, laptopCase.getWeight(), laptopCaseWeightRow);
        setLabelText(laptopCaseDimensionsText, laptopCase.getDimensions(), laptopCaseDimensionsRow);
        setLabelText(laptopCaseMaterialText, laptopCase.getMaterial(), laptopCaseMaterialRow);
    }

    private void displayProductDescription(List<String[]> productDescription) {
        for (int i = 0; i < productDescription.size(); i++) {
            String s = productDescription.get(i)[0];
            Label productDescriptionLabel = new Label(productDescription.get(i)[1]);
            productDescriptionLabel.setWrapText(true);
            if (s.equals("header")) {
                productDescriptionLabel.getStyleClass().add("description-header");
            } else if (s.equals("bold")) {
                productDescriptionLabel.getStyleClass().add("description-bold");
            } else {
                productDescriptionLabel.getStyleClass().add("description-normal");
            }
            descriptionBox.getChildren().add(productDescriptionLabel);
        }
    }


    private void displayProductReview(List<Review> reviews) {
        if (!reviews.isEmpty()) {
            Label userReviewsHeader = new Label("User reviews:");
            userReviewsHeader.getStyleClass().add("section-header");
            productPage.getChildren().add(userReviewsHeader);
            for (Review review : reviews) {
                VBox vbox = new VBox();
                vbox.getStyleClass().add("review-box");
                Label dateLabel = new Label(review.getCreatedDate().toString());
                dateLabel.getStyleClass().add("review-date");
                Label contentLabel = new Label(review.getContent());
                contentLabel.setWrapText(true);
                contentLabel.getStyleClass().add("review-content");
                Label scoreLabel = new Label(review.getScore() + "⭐");
                scoreLabel.getStyleClass().add("review-score");
                Label usernameLabel = new Label(review.getUsername());
                usernameLabel.getStyleClass().add("review-username");
                vbox.getChildren().addAll(dateLabel, contentLabel, scoreLabel, usernameLabel);
                productPage.getChildren().add(vbox);
            }
        }
    }
}
