package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.ProductDatabase;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.List;

public class FavouriteGridController extends ProductGridController {

    @FXML
    Button expandButton;

    public FavouriteGridController() {
        super(
                FXCollections.observableArrayList(),
                FXCollections.observableArrayList()
        );
        laptops.addAll(ProductDatabase.getInstance().getFavouriteProducts());
    }

    @Override
    public void initialize() {
        super.initialize();
        removeQueryListener();
        List<Laptop> nextPage = fetchNextPage(0);
        expandButton.setDisable(true);
        expandButton.setManaged(false);
    }

    @Override
    public void expand() {
    }
}