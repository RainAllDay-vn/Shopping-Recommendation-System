package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.ShoppingApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TopBarController {
    private final Image appLogo = new Image(ShoppingApplication.class.getResource("images/app-icon.jpg").toExternalForm());
    @FXML private Button goToList;
    @FXML private ImageView logo;
    @FXML private Label titleLabel;

    @FXML
    public void initialize() {
        goToList.setOnAction(e -> MainPageController.getInstance().displayMyList());
        logo.setImage(appLogo);
        logo.setOnMouseClicked(e -> MainPageController.getInstance().displayMain());
        titleLabel.setOnMouseClicked(e -> MainPageController.getInstance().displayMain());
    }
}
