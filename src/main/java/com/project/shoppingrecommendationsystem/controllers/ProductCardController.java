package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.models.Product;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ProductCardController {

     @FXML
     private VBox productCardContainer;

     @FXML
     private ImageView productImage;

     @FXML
     private Label productName;

     @FXML
     private Label productPrice;

     @FXML
     private Label productDiscount;

     @FXML
     private Button viewDetailsButton;

     private Product product;

     public ProductCardController(Product product) {
          this.product = product;

          FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/project/shoppingrecommendationsystem/product-card.fxml"));
          fxmlLoader.setController(this);
          try {
               fxmlLoader.load();
          } catch (IOException e) {
               e.printStackTrace();
          }
     }

     @FXML
     public void initialize() {
          if (product != null) {
               productImage.setImage(new Image(product.getOverview().get("productImage")));
               productName.setText(product.getOverview().get("name"));
               productPrice.setText("Price: " + product.getOverview().get("price"));
               productDiscount.setText("Discount: " + product.getOverview().get("discount"));
          }

          viewDetailsButton.setOnAction(e -> goToProductPage(product));
     }

     public void goToProductPage(Product product) {
          // Implement navigation logic here
          System.out.println("Navigating to product page for: " + product.getOverview().get("name"));
     }

     public VBox getProductCardContainer() {
          return productCardContainer;
     }
}
