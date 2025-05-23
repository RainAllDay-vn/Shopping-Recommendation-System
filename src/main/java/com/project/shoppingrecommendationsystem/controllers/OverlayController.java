package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.Messenger;
import com.project.shoppingrecommendationsystem.ShoppingApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class OverlayController implements Initializable {
    private final MainPageController mainPageController = Messenger.getInstance().getMainPageController();
    private final Image chatLogo = new Image(Objects.requireNonNull(
            ShoppingApplication.class.getResource("images/chat-bot-icon.png")).toExternalForm());
    @FXML
    private ImageView chatButton;

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        chatButton.setImage(chatLogo);
        chatButton.setOnMouseClicked(event -> toggleChatBox());
    }

    @FXML
    public void toggleChatBox () {
        mainPageController.toggleChatBox();
    }
}
