package com.project.shoppingrecommendationsystem.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class FooterBarController implements Initializable {

    @FXML
    Button footerLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
