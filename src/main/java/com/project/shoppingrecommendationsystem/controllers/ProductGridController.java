package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.Laptop;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ProductGridController implements Initializable{

    @FXML
    private VBox rootVBox;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button expandButton;

    private static final int PRODUCTS_PER_PAGE = 6;
    private static final int COLUMNS = 3;
    private Product[] allProducts;
    private List<VBox> displayedCards = new ArrayList<>();
    private int currentProductIndex = 0;

    public void setProducts(Product[] products) {
        if (products == null || products.length == 0) {
            System.err.println("No products available.");
            return;
        }
        this.allProducts = products;
        currentProductIndex = 0; // Reset index when setting new products
        displayedCards.clear();
        gridPane.getChildren().clear(); // Clear grid before loading new products
        loadMoreProducts();
    }

    @FXML
    private void expandProducts() {
        loadMoreProducts();
    }

    private void loadMoreProducts() {

        int column = displayedCards.size() % COLUMNS;
        int row = displayedCards.size() / COLUMNS;

        for (int i = 0; i < PRODUCTS_PER_PAGE && currentProductIndex < allProducts.length; i++, currentProductIndex++) {
            VBox productCard = createProductCard(allProducts[currentProductIndex]);

            if (productCard != null) {
                displayedCards.add(productCard);
                gridPane.add(productCard, column, row);

                column++;
                if (column == COLUMNS) {
                    column = 0;
                    row++;
                }
            }
        }

        // Hide expand button if all products are loaded
        //expandButton.setVisible(currentProductIndex < allProducts.length);
    }

    private VBox createProductCard(Product product) {
        try {
            FXMLLoader card = new FXMLLoader(getClass().getResource
                    ("/com/project/shoppingrecommendationsystem/components/product-card.fxml"));
            VBox productCard = card.load();
            ProductCardController controller = card.getController();
            controller.setProduct(product);
            return productCard;
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null instead of an empty VBox for better error handling
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allProducts = new Product[PRODUCTS_PER_PAGE];
        for(int i = 0; i < PRODUCTS_PER_PAGE; i++) {
            allProducts[i] = Laptop.buildTestLaptop();
        }
        loadMoreProducts();
    }
}
