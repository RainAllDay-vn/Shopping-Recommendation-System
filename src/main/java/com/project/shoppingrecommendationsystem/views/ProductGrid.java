package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.ProductGridController;

public class ProductGrid extends View<ProductGridController> {
    public ProductGrid() {
        root = load("components/product-grid.fxml", new ProductGridController());
    }
}
