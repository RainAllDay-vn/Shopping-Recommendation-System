package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.ProductCardController;
import com.project.shoppingrecommendationsystem.models.Product;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;

public class RenderProductCard extends Button {

    private ProductCardController productCardController;

    public RenderProductCard(Product product) {
        LinkedHashMap<String, String> overview = product.getOverview();
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-border-color: gray; -fx-border-radius: 10;");

        // Product Image
        ImageView productImage = new ImageView(new Image(overview.get("productImage"), 120, 120, true, true));
        productImage.setPreserveRatio(true);

        // Product Details
        Label productName = new Label(overview.get("name"));
        productName.setStyle("-fx-font-weight: bold;");

        Label productPrice = new Label("Price: " + overview.get("price"));
        Label productDiscount = new Label("Discount: " + overview.get("price"));
        productDiscount.setStyle("-fx-text-fill: green;");

        vbox.getChildren().addAll(productImage, productName, productPrice, productDiscount);
        this.getChildren().add(vbox);
        this.setOnMouseClicked(e -> {productCardController.goToProductPage(product);});
    }
}
