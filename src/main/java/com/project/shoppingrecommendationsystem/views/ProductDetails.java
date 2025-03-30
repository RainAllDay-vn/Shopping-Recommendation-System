package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.controllers.ProductDetailsController;
import com.project.shoppingrecommendationsystem.models.Laptop;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ProductDetails extends View {
    private final ProductDetailsController controller;
    public ProductDetails(Laptop product) {
        String path = "components/product-details.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(ShoppingApplication.class.getResource(path));
        Node root;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.err.println("[ERROR] Could not load " + path);
            System.out.println(e.getMessage());
            root = new VBox();
        } catch (NullPointerException e) {
            System.err.println("[ERROR] Could not find " + path);
            System.out.println(e.getMessage());
            root = new VBox();
        }
        this.root = root;
        this.controller = fxmlLoader.getController();
        this.controller.setProductDetails(product);
    }
}