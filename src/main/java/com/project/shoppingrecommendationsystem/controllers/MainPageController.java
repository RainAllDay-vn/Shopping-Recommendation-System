package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.views.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class MainPageController {
    @FXML
    private BorderPane root;
    @FXML private Node productGrid;
    private Node filterBar;
    private Node accessBar;
    private static MainPageController instance;

    @FXML
    public void initialize() {
        instance = this;
        productGrid = new ProductGrid().getRoot();
        filterBar = new FilterBar().getRoot();
        accessBar = new AccessBar().getRoot();

        root.setLeft(filterBar);
        root.setCenter(productGrid);
        root.setTop(new TopBar().getRoot());
        root.setRight(new ChatBox().getRoot());
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
}
