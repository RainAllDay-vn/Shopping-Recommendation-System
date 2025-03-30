package com.project.shoppingrecommendationsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AccessBarController {
    @FXML
    Button goToMainButton;

    @FXML
    public void goToMain() {
        MainPageController.getInstance().displayMain();
    }
}
