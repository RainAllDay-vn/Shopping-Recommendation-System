package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.Messenger;
import com.project.shoppingrecommendationsystem.Messenger;
import com.project.shoppingrecommendationsystem.views.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
    @FXML
    private BorderPane root;
    private final ChatBox chatBox;

    public MainPageController() {
        chatBox = new ChatBox();
        Messenger.getInstance().setMainPageController(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.setTop(new TopBar().getRoot());
        root.setLeft(new FilterBar().getRoot());
        root.setBottom(new FooterBar().getRoot());
        root.setCenter(new ProductGrid().getRoot());
    }

    public void toggleChatBox () {
        if (root.getRight() != null) {
            root.setRight(null);
        } else {
            root.setRight(chatBox.getRoot());
        }
    }
}
