package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.models.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public class ProductCardController {

     @FXML
     private ImageView productImage;
     @FXML
     private Label productName;
     @FXML
     private Label productPrice;
     @FXML
     private Label productDiscount;
     private Product product;

     public void setProduct(Product product) {
          this.product = product;
          URL defaultImage = ShoppingApplication.class.getResource("product-default.png");
          if (product.getProductImage() != null) {
               System.out.println(product.getProductImage());
               Image image = new Image(product.getProductImage());
               if (image.isError() && defaultImage != null) {
                    productImage.setImage(new Image(defaultImage.toString()));
               } else {
                    productImage.setImage(image);
               }
          } else if (defaultImage != null) {
               productImage.setImage(new Image(defaultImage.toString()));
          }
          productName.setText(product.getName());
          productPrice.setText(String.valueOf(product.getPrice()));
          productDiscount.setText(String.valueOf(product.getDiscountPrice()));
     }
}
