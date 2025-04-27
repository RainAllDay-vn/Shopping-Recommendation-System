package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.models.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;

public class ProductCardController {
     private static final Image defaultImage = loadDefaultImage();
     @FXML
     private ImageView productImage;
     @FXML
     private Label productName;
     @FXML
     private Label productPrice;
     @FXML
     private Label productDiscount;

    public void setProduct(Product product) {
        try {
            File imageFile = new File(product.getProductImage());
            Image image = new Image(imageFile.toURI().toString());
            assert !image.isError();
            productImage.setImage(image);
        } catch (Exception e) {
            System.err.printf("[ERROR] Loading product image failed (Path:%s)\n", product.getProductImage());
            productImage.setImage(defaultImage);
        }
        productName.setText(product.getName());
        productPrice.setText(String.valueOf(product.getPrice()));
        productDiscount.setText(String.valueOf(product.getDiscountPrice()));
     }

     private static Image loadDefaultImage() {
          URL imageURL = ShoppingApplication.class.getResource("product-default.png");
          try {
              assert imageURL != null;
              return new Image(imageURL.toString());
          } catch (Exception e) {
               throw new RuntimeException("[FATAL] : Error loading default image");
          }
     }
}
