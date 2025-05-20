package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.ProductCardController;
import com.project.shoppingrecommendationsystem.models.Product;

public class ProductCard extends View{

    public ProductCard(Product product) {
        root = load("components/product-card.fxml");
        getController().setProduct(product);
        getController().setRoot(root);
    }

    public ProductCardController getController() {
        return getFxmlLoader().getController();
    }
}
