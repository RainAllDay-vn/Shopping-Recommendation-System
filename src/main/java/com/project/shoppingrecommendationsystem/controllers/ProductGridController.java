package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.Messenger;
import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.database.LaptopDatabase;
import com.project.shoppingrecommendationsystem.views.ProductCard;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ProductGridController implements Initializable {
    private static final int PRODUCT_PER_PAGE = 12;
    private final LaptopDatabase productDatabase;
    private final ObservableList<Laptop> laptops;
    private final ObservableList<String[]> query = Messenger.getInstance().getQuery();

    @FXML private FlowPane flowPane;
    @FXML private Button expandButton;

    public ProductGridController() {
        this.productDatabase = LaptopDatabase.getInstance();
        this.laptops = FXCollections.observableArrayList(productDatabase.findLaptops(query, PRODUCT_PER_PAGE, 0));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        laptops.forEach(laptop -> {
                    ProductCard card = new ProductCard(laptop);
                    flowPane.getChildren().add(card.getRoot());
                }
        );
        laptops.addListener((InvalidationListener) observable -> {
            flowPane.getChildren().clear();
            for (Laptop laptop : laptops) {
                flowPane.getChildren().add(new ProductCard(laptop).getRoot());
            }
        });
        query.addListener((InvalidationListener) observable -> {
            laptops.clear();
            laptops.addAll(productDatabase.findLaptops(query, PRODUCT_PER_PAGE, 0));
        });
        expandButton.setOnAction(event -> expand());
    }

    protected void updateProductCards() {
        flowPane.getChildren().clear();
        laptops.forEach(laptop -> {
                    ProductCard card = new ProductCard(laptop);
                    flowPane.getChildren().add(card.getRoot());
                }
        );
    }

    public void expand() {
        laptops.addAll(productDatabase.findLaptops(query, PRODUCT_PER_PAGE, laptops.size()));
    }
}
