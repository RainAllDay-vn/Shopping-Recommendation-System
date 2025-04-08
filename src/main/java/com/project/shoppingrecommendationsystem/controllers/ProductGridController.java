package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.Messenger;
import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.ProductDatabase;
import com.project.shoppingrecommendationsystem.views.ProductCard;
import com.project.shoppingrecommendationsystem.views.ProductDetails;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import java.util.List;

public class ProductGridController {
    protected static final int PRODUCTS_PER_PAGE = 12;
    protected final ProductDatabase productDatabase;
    protected final ObservableList<Laptop> laptops;
    protected final ObservableList<String[]> query;
    protected InvalidationListener queryListener;

    @FXML private FlowPane flowPane;
    @FXML private Button expandButton;

    public ProductGridController() {
        this(Messenger.getInstance().getQuery());
    }

    public ProductGridController(ObservableList<String[]> query, ObservableList<Laptop> initialLaptops) {
        this.productDatabase = ProductDatabase.getInstance();
        this.query = query;
        this.laptops = FXCollections.observableArrayList(initialLaptops);
    }

    public ProductGridController(ObservableList<String[]> query) {
        this.productDatabase = ProductDatabase.getInstance();
        this.query = query;
        this.laptops = FXCollections.observableArrayList(fetchNextPage(0));
    }

    @FXML
    public void initialize() {
        updateProductCards();
        setupListeners();
        expandButton.setOnAction(event -> expand());
    }

    protected void setupListeners() {
        laptops.addListener((InvalidationListener) observable -> updateProductCards());

        queryListener = observable -> {
            laptops.clear();
            laptops.addAll(fetchNextPage(0));
        };
        query.addListener(queryListener);
    }

    protected void updateProductCards() {
        flowPane.getChildren().clear();
        laptops.forEach(laptop -> {
            ProductCard card = new ProductCard(laptop, event -> goToProductDetails(laptop));
            flowPane.getChildren().add(card.getRoot());
            }
        );
    }


    public void goToProductDetails(Laptop product) {
        ProductDetails productDetails = new ProductDetails(product);
        MainPageController.getInstance().displayDetails(productDetails.getRoot());
    }

    protected List<Laptop> fetchNextPage(int offset) {
        return productDatabase.findLaptops(query, PRODUCTS_PER_PAGE, offset);
    }

    public void expand() {
        List<Laptop> nextPage = fetchNextPage(laptops.size());
        laptops.addAll(nextPage);
    }

    protected void removeQueryListener() {
        query.removeListener(queryListener);
    }
}