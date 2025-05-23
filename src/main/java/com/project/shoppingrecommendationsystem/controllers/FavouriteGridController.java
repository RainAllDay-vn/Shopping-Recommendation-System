package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.database.LaptopDatabase;
import com.project.shoppingrecommendationsystem.views.ProductCard;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FavouriteGridController implements Initializable {

    @FXML private FlowPane flowPane;
    private final List<Product> products;

    public FavouriteGridController() {
        this.products = LaptopDatabase.getInstance().getFavouriteProducts();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        products.forEach(laptop -> {
                    ProductCard card = new ProductCard(laptop);
                    flowPane.getChildren().add(card.getRoot());
                }
        );
    }
}