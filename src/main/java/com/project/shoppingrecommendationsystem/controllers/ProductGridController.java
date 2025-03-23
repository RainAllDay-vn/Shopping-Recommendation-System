package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.models.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ProductGridController {

    @FXML
    private VBox rootVBox;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button expandButton;

    private static final int PRODUCTS_PER_PAGE = 6;
    private static final int COLUMNS = 3;
    private Product[] allProducts;
    private List<HBox> displayedCards = new ArrayList<>();
    private int currentProductIndex = 0;

    public void setProducts(Product[] products) {
        this.allProducts = products;
        loadMoreProducts();
    }

    @FXML
    public void expandProducts() {
        loadMoreProducts();
    }

    private void loadMoreProducts() {
        int column = displayedCards.size() % COLUMNS;
        int row = displayedCards.size() / COLUMNS;

        for (int i = 0; i < PRODUCTS_PER_PAGE && currentProductIndex < allProducts.length; i++, currentProductIndex++) {
            HBox productCard = createProductCard(allProducts[currentProductIndex]);
            displayedCards.add(productCard);
            gridPane.add(productCard, column, row);

            column++;
            if (column == COLUMNS) {
                column = 0;
                row++;
            }
        }

        if (currentProductIndex >= allProducts.length) {
            expandButton.setVisible(false);
        }
    }

    private HBox createProductCard(Product product) {
        HBox card = new HBox();
        card.setSpacing(10);
        card.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-border-color: gray; -fx-border-radius: 10;");

        // Product Image
        ImageView productImage = new ImageView(new Image(product.getOverview().get("productImage"), 120, 120, true, true));
        productImage.setPreserveRatio(true);

        // Product Details
        VBox details = new VBox();
        Label productName = new Label(product.getOverview().get("name"));
        productName.setStyle("-fx-font-weight: bold;");

        Label productPrice = new Label("Price: " + product.getOverview().get("price"));
        Label productDiscount = new Label("Discount: " + product.getOverview().get("discount"));
        productDiscount.setStyle("-fx-text-fill: green;");

        details.getChildren().addAll(productName, productPrice, productDiscount);
        card.getChildren().addAll(productImage, details);

        return card;
    }

    public VBox getRootVBox() {
        return rootVBox;
    }
}
