package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.Messenger;
import com.project.shoppingrecommendationsystem.views.*;
import javafx.fxml.FXML;
<<<<<<< HEAD
import javafx.scene.Node;
=======
import javafx.fxml.Initializable;
>>>>>>> 3fb4f8371ec22213294e88dd2c87cd5ea4e8b321
import javafx.scene.layout.BorderPane;

public class MainPageController {
    @FXML
    private BorderPane root;
    @FXML private Node productGrid;
    private Node filterBar;
    private Node accessBar;
    private static MainPageController instance;

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

    public MainPageController() {
        chatBox = new ChatBox();
        Messenger.getInstance().setMainPageController(this);
    }
>>>>>>> 3fb4f8371ec22213294e88dd2c87cd5ea4e8b321

        root.setLeft(filterBar);
        root.setCenter(productGrid);
        root.setTop(new TopBar().getRoot());
<<<<<<< HEAD
        root.setRight(new ChatBox().getRoot());
=======
        root.setLeft(new FilterBar().getRoot());
>>>>>>> 3fb4f8371ec22213294e88dd2c87cd5ea4e8b321
        root.setBottom(new FooterBar().getRoot());
    }

    public static MainPageController getInstance(){
        return instance;
    }

    public void displayDetails(Node content){
        root.setCenter(content);
        root.setLeft(accessBar);
    }

    public void displayMain(){
        root.setLeft(filterBar);
        root.setCenter(productGrid);
    }

    public void displayMyList(){
        root.setCenter(new FavouriteGrid().getRoot());
        root.setLeft(accessBar);
    }

    public void toggleChatBox () {
        if (root.getRight() != null) {
            root.setRight(null);
        } else {
            root.setRight(chatBox.getRoot());
        }
    }
}
