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
    @FXML private BorderPane root;
    private final ChatBox chatBox;
    private final ProductGrid productGrid;
    private final FilterBar filterBar;

    public MainPageController() {
        productGrid = new ProductGrid();
        filterBar = new FilterBar();
        chatBox = new ChatBox();
        Messenger.getInstance().setMainPageController(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.setTop(new TopBar().getRoot());
        root.setBottom(new FooterBar().getRoot());
        root.setLeft(filterBar.getRoot());
        root.setCenter(productGrid.getRoot());
    }

    public void toggleChatBox () {
        if (root.getRight() != null) {
            root.setRight(null);
        } else {
            root.setRight(chatBox.getRoot());
        }
    }

    public void displayDetails(Node content){
        root.setCenter(content);
        root.setLeft(null);
    }

    public void displayMain(){
        root.setLeft(filterBar.getRoot());
        productGrid.getController().updateProductCards();
        root.setCenter(productGrid.getRoot());
    }

    public void displayMyList(){
        root.setCenter(new FavouriteGrid().getRoot());
        root.setLeft(null);
    }
}
