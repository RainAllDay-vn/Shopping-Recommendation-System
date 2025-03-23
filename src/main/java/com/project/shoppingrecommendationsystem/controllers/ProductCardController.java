package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.models.Laptop;
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

     private Product product;

     public ProductCardController() {
          // No need to load FXML here, JavaFX does it automatically
     }

     @FXML
     public void initialize() {
          // Add click event
          productCardContainer.setOnMouseClicked(e -> goToProductPage(product));
     }

     public void display() {
          if (product != null) {
               Image img = new Image(getClass().getResource("/com/project/shoppingrecommendationsystem/app-icon.jpg").toExternalForm());

               productImage.setImage(img);
               productName.setText(product.getOverview().get("name"));
               productPrice.setText("Price: " + product.getOverview().get("price"));
               productDiscount.setText("Discount: " + product.getOverview().get("discount"));
          }
     }

     public void goToProductPage(Product product) {
          System.out.println("Navigating to product page for: " + product.getOverview().get("name"));
     }

     public void setProduct(Product product) {
          this.product = product;
          display();  // Call display after setting the product
     }
}
