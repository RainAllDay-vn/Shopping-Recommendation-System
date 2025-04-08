package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.FavouriteGridController;

public class FavouriteGrid extends View<FavouriteGridController> {
    public FavouriteGrid() {
        root = load("components/favourite-grid.fxml", new FavouriteGridController());
    }
}
