package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.ProductGridController;

public class ProductGrid extends View {
    public ProductGrid() {
        root = load("components/product-grid.fxml");
    }

    public ProductGridController getController() {
        return getFxmlLoader().getController();
    }
}
