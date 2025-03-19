package com.project.shoppingrecommendationsystem.controller;

import com.project.shoppingrecommendationsystem.models.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProductCardController {
    @FXML
    private ImageView productImage;
    @FXML
    private Label productName;
    @FXML
    private Label productPrice;
    @FXML
    private Label productDiscount;
    @FXML
    private Button compareButton;

    public void getProductData(Product product) {

    }
}
