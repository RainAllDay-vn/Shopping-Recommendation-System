package com.project.shoppingrecommendationsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.Product;


public class SearchController {

    @FXML
    private HBox topBar;

    @FXML
    private ImageView logo;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    public void initialize() {
        // Set logo image
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/com/project/shoppingrecommendationsystem/app-icon.jpg")).toExternalForm());
        logo.setImage(image);

        // Handle enter key for search
        searchTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSearch();
            }
        });
    }

    @FXML
    public void handleSearch() {
        String searchText = searchTextField.getText().trim();
        if (!searchText.isEmpty()) {
            System.out.println("Searching for: " + searchText + FilterBarController.getFilterData().toString());
            //Test
            List<Product> products = new LinkedList<>();
            for(int i = 0; i< 10; i++){
                products.add(Laptop.buildTestLaptop());
            }
            ProductGridController.clear();
            ProductGridController.addProducts(products);
            //End test
            // Implement search logic here
        }
    }
}
