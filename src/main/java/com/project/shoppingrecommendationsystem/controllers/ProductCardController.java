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

import java.awt.event.ActionEvent;
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
     private Button showDetailsButton;

     private Product product;

     @FXML
     public void goToProductPage() {
          if (this.product != null) {
               NavigationController.goToProduct(this.product);
          }
     }

     @FXML
     public void addToList() {
          System.out.println("Add to list" + this.product.getOverview());
     }

     public void setProduct(Product product) {
          this.product = product;

          if (product != null) {
               Image img = new Image(getClass().getResource("/com/project/shoppingrecommendationsystem/app-icon.jpg").toExternalForm());

               productImage.setImage(img);
               productName.setText(product.getOverview().get("name"));
               productPrice.setText("Price: " + product.getOverview().get("price"));
               productDiscount.setText("Discount: " + product.getOverview().get("discount"));
          }
     }

}
