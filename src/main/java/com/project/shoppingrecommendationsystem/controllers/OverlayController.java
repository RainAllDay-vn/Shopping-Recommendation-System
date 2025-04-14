package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.Messenger;
import javafx.fxml.FXML;

public class OverlayController {
    private final MainPageController mainPageController = Messenger.getInstance().getMainPageController();

    @FXML
    public void toggleChatBox () {
        mainPageController.toggleChatBox();
    }
}
