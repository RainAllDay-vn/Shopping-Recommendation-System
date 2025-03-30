package com.project.shoppingrecommendationsystem.controllers;

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
    private Node productGrid;
    private Node filterBar;
    private Node accessBar;
    private static MainPageController instance;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
}
