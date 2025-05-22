package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.Messenger;
import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.ProductDatabase;
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
    private final ProductDatabase productDatabase = ProductDatabase.getInstance();
    private final ObservableList<String[]> query = Messenger.getInstance().getQuery();
    private final ObservableList<Laptop> laptops = FXCollections.observableArrayList(productDatabase.findLaptops(query, PRODUCT_PER_PAGE, 0));

    @FXML
    private FlowPane flowPane;
    @FXML
    private Button expandButton;
    @FXML
    private Button sortByNameButton;
    @FXML
    private Button sortByPriceButton;
    @FXML
    private Button sortByDiscountPriceButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sortByNameButton.setOnAction(event -> sortByName());
        sortByPriceButton.setOnAction(event -> sortByPrice());
        sortByDiscountPriceButton.setOnAction(event -> sortByDiscountPrice());

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
            if(laptops.isEmpty()){
                expandButton.setDisable(true);
                expandButton.setManaged(false);
            } else {
                expandButton.setDisable(false);
                expandButton.setManaged(true);
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

    private void sortByName() {
        laptops.clear();
        productDatabase.sortByName();
        laptops.addAll(productDatabase.findLaptops(query, PRODUCT_PER_PAGE, 0));
        updateProductCards();
    }

    private void sortByPrice() {
        laptops.clear();
        productDatabase.sortByPrice();
        laptops.addAll(productDatabase.findLaptops(query, PRODUCT_PER_PAGE, 0));
        updateProductCards();
    }

    private void sortByDiscountPrice() {
        laptops.clear();
        productDatabase.sortByDiscountPrice();
        laptops.addAll(productDatabase.findLaptops(query, PRODUCT_PER_PAGE, 0));
        updateProductCards();
    }

    public void expand() {
        laptops.addAll(productDatabase.findLaptops(query, PRODUCT_PER_PAGE, laptops.size()));
    }
}
