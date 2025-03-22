package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.ProductGridController;
import com.project.shoppingrecommendationsystem.models.Product;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class RenderProductGrid extends VBox {
    private static final int PRODUCTS_PER_PAGE = 6; // Load 6 products per expansion
    private static final int COLUMNS = 3; // 3 columns per row
    private Product[] allProducts;
    private List<RenderProductCard> displayedCards = new ArrayList<>();
    private GridPane gridPane;
    private Button expandButton;
    private int currentProductIndex = 0;

    public RenderProductGrid(Product[] products) {
        this.allProducts = products;

        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        expandButton = new Button("<Expand>");
        expandButton.setOnAction(event -> ProductGridController.expandProducts(this));

        loadMoreProducts(); // Initial load

        this.getChildren().addAll(gridPane, expandButton);
    }

    public void loadMoreProducts() {
        int column = displayedCards.size() % COLUMNS;
        int row = displayedCards.size() / COLUMNS;

        for (int i = 0; i < PRODUCTS_PER_PAGE && currentProductIndex < allProducts.length; i++, currentProductIndex++) {
            RenderProductCard card = new RenderProductCard(allProducts[currentProductIndex]);
            displayedCards.add(card);
            gridPane.add(card, column, row);

            column++;
            if (column == COLUMNS) {
                column = 0;
                row++;
            }
        }

        // Ensure button is always at the bottom
        this.getChildren().remove(expandButton);
        this.getChildren().add(expandButton);

        // Hide button if all products are loaded
        if (currentProductIndex >= allProducts.length) {
            expandButton.setVisible(false);
        }
    }

    public GridPane getGridPane() {
        return gridPane;
    }
}
