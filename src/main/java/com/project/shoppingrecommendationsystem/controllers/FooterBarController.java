package com.project.shoppingrecommendationsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.awt.*;
import java.net.URI;

public class FooterBarController {

    @FXML
    Button footerLabel;

    @FXML
    public void initialize() {
        footerLabel.setOnMouseClicked(event -> {goToRepo();});
    }

    public void goToRepo(){
        String url = "https://github.com/RainAllDay-vn/Shopping-Recommendation-System";
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
