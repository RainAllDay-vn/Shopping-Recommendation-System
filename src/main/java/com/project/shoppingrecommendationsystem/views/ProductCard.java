package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.ProductCardController;
import com.project.shoppingrecommendationsystem.models.Laptop;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ProductCard extends View<ProductCardController> {
    public ProductCard(Laptop product, EventHandler<ActionEvent> handler) {
        ProductCardController controller = new ProductCardController();
        root = load("components/product-card.fxml", controller);
        controller.setProduct(product);
        controller.setOnShowMore(handler);
    }
}