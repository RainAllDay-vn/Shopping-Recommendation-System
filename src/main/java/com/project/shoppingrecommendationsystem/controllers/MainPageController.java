package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.Messenger;
import com.project.shoppingrecommendationsystem.views.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
    @FXML
    private BorderPane root;
    private final ChatBox chatBox;
    private final FilterBar filterBar;
    private final ProductGrid productGrid;

    public MainPageController() {
        chatBox = new ChatBox();
        filterBar = new FilterBar();
        productGrid = new ProductGrid();
        Messenger.getInstance().setMainPageController(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.setTop(new TopBar().getRoot());
        root.setBottom(new FooterBar().getRoot());
        root.setLeft(filterBar.getRoot());
        root.setCenter(productGrid.getRoot());
    }

    public void displayDetails(Node content){
        root.setCenter(content);
        root.setLeft(accessBar);
    }

    public void displayMain(){
        root.setLeft(filterBar.getRoot());
        root.setCenter(productGrid.getRoot());
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
