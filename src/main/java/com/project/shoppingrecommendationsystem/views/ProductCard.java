package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.controllers.ProductCardController;
import com.project.shoppingrecommendationsystem.models.Product;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

import java.io.IOException;

public class ProductCard extends View{
    private final ProductCardController controller;

    public ProductCard(Product product) {
        String path = "components/product-card.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(ShoppingApplication.class.getResource(path));
        Node root;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.err.println("[ERROR] Could not load " + path);
            System.out.println(e.getMessage());
            root = new FlowPane();
        } catch (NullPointerException e) {
            System.err.println("[ERROR] Could not find " + path) ;
            System.out.println(e.getMessage());
            root = new FlowPane();
        }
        this.root = root;
        this.controller = fxmlLoader.getController();
        this.controller.setProduct(product);
    }

}
