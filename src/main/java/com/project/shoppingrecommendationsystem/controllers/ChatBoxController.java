package com.project.shoppingrecommendationsystem.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

import com.project.shoppingrecommendationsystem.ShoppingApplication;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ChatBoxController implements Initializable {

    @FXML
    private TextField chatInput;
    @FXML
    private Button sendButton;

    private Image userImage;
    private Image botImage;

    public ChatBoxController() {
        URL userImageURL = ShoppingApplication.class.getResource("images/user-icon.png");
        URL botImageURL = ShoppingApplication.class.getResource("images/bot-icon.png");
        try {
            assert userImageURL != null;
            userImage = new Image(userImageURL.toExternalForm());
            assert botImageURL != null;
            botImage = new Image(botImageURL.toExternalForm());
        } catch (Exception e) {
            System.err.println("[ERROR] : Can not load user or bot chat icon.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}