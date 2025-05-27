package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.Messenger;
import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.database.ProductDatabase;
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
    private final ProductDatabase productDatabase = Messenger.getInstance().getProductDatabase();
    private final ObservableList<String[]> query = Messenger.getInstance().getQuery();
    private final ObservableList<Product> products = FXCollections.observableArrayList
            (productDatabase.findProducts(query, PRODUCT_PER_PAGE, 0));

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

        products.forEach(product -> {
                    ProductCard card = new ProductCard(product);
                    flowPane.getChildren().add(card.getRoot());
                }
        );
        products.addListener((InvalidationListener) observable -> {
            flowPane.getChildren().clear();
            for (Product product : products) {
                flowPane.getChildren().add(new ProductCard(product).getRoot());
            }
            if(products.isEmpty()){
                expandButton.setVisible(false);
                expandButton.setManaged(false);
            } else {
                expandButton.setVisible(true);
                expandButton.setManaged(true);
            }
        });
        query.addListener((InvalidationListener) observable -> {
            products.clear();
            products.addAll(productDatabase.findProducts(query, PRODUCT_PER_PAGE, 0));
        });
        expandButton.setOnAction(event -> expand());
    }

    protected void updateProductCards() {
        flowPane.getChildren().clear();
        products.forEach(laptop -> {
                    ProductCard card = new ProductCard(laptop);
                    flowPane.getChildren().add(card.getRoot());
                }
        );
    }

    private void sortByName() {
        productDatabase.sortByName();
        products.setAll(productDatabase.findProducts(query, PRODUCT_PER_PAGE, 0));
    }

    private void sortByPrice() {
        productDatabase.sortByPrice();
        products.setAll(productDatabase.findProducts(query, PRODUCT_PER_PAGE, 0));
    }

    private void sortByDiscountPrice() {
        productDatabase.sortByDiscountPrice();
        products.setAll(productDatabase.findProducts(query, PRODUCT_PER_PAGE, 0));
    }

    public void expand() {
        products.addAll(productDatabase.findProducts(query, PRODUCT_PER_PAGE, products.size()));
    }
}
