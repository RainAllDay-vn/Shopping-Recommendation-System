package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.ProductDetailsController;
import com.project.shoppingrecommendationsystem.models.Laptop;

public class ProductDetails extends View<ProductDetailsController> {
    public ProductDetails(Laptop product) {
        ProductDetailsController controller = new ProductDetailsController();
        root = load("components/product-details.fxml", controller);
        controller.setProductDetails(product);
    }
}