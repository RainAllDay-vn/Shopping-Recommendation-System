package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.views.*;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
    public BorderPane root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.setTop(new TopBar().getRoot());
        root.setLeft(new FilterBar().getRoot());
        root.setRight(new ChatBox().getRoot());
        root.setBottom(new FooterBar().getRoot());
        root.setCenter(new ProductGrid().getRoot());
    }
}
