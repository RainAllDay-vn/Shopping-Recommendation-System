package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.Messenger;
import com.project.shoppingrecommendationsystem.views.*;
import javafx.fxml.FXML;
<<<<<<< HEAD
import javafx.fxml.Initializable;
=======
<<<<<<< HEAD
import javafx.scene.Node;
=======
import javafx.fxml.Initializable;
>>>>>>> 3fb4f8371ec22213294e88dd2c87cd5ea4e8b321
>>>>>>> d828c32cb350deece54358bda09faf9f6a7b28c1
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
    @FXML
    private BorderPane root;
    private final ChatBox chatBox;

<<<<<<< HEAD
=======
<<<<<<< HEAD
    @FXML
    public void initialize() {
        instance = this;
        productGrid = new ProductGrid().getRoot();
        filterBar = new FilterBar().getRoot();
        accessBar = new AccessBar().getRoot();
=======
public class MainPageController implements Initializable {
    @FXML
    private BorderPane root;
    private final ChatBox chatBox;

>>>>>>> d828c32cb350deece54358bda09faf9f6a7b28c1
    public MainPageController() {
        chatBox = new ChatBox();
        Messenger.getInstance().setMainPageController(this);
    }
<<<<<<< HEAD
=======
>>>>>>> 3fb4f8371ec22213294e88dd2c87cd5ea4e8b321
>>>>>>> d828c32cb350deece54358bda09faf9f6a7b28c1

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.setTop(new TopBar().getRoot());
<<<<<<< HEAD
        root.setLeft(new FilterBar().getRoot());
=======
<<<<<<< HEAD
        root.setRight(new ChatBox().getRoot());
=======
        root.setLeft(new FilterBar().getRoot());
>>>>>>> 3fb4f8371ec22213294e88dd2c87cd5ea4e8b321
>>>>>>> d828c32cb350deece54358bda09faf9f6a7b28c1
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

    public void toggleChatBox () {
        if (root.getRight() != null) {
            root.setRight(null);
        } else {
            root.setRight(chatBox.getRoot());
        }
    }
}
