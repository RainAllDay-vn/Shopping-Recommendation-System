package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.ProductDatabase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;

public class ProductCardController {
     private static final Image defaultImage = loadDefaultImage();

     @FXML private ImageView productImage;
     @FXML private Label productName;
     @FXML private Label productPrice;
     @FXML private Label productDiscount;
     @FXML private Button showMoreButton;
     @FXML private Button toggleFavouriteStatusButton;


     private Laptop product;

    public void setProduct(Laptop product) {
        this.product = product;
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
        boolean isFavorite = ProductDatabase.getInstance().isFavourite(product);
        if (isFavorite) {
            toggleFavouriteStatusButton.setText("Unlike");
        } else {
            toggleFavouriteStatusButton.setText("Like");
        }
    }

     private static Image loadDefaultImage() {
          URL imageURL = ShoppingApplication.class.getResource("images/product-default.png");
          try {
              assert imageURL != null;
              return new Image(imageURL.toString());
          } catch (Exception e) {
               throw new RuntimeException("[FATAL] : Error loading default image");
          }
     }

     @FXML
     private void initialize() {
         toggleFavouriteStatusButton.setOnAction(event -> toggleFavouriteStatus());
     }

    private void toggleFavouriteStatus() {
        boolean isFavorite = ProductDatabase.getInstance().isFavourite(product);

        if (isFavorite) {
            removeFromFavorites();
        } else {
            addToFavorites();
        }
    }

    private void addToFavorites() {
        updateFavoriteStatus(true);
    }

    private void removeFromFavorites() {
        updateFavoriteStatus(false);
    }

    private void updateFavoriteStatus(boolean isAdding) {
        if (isAdding) {
            ProductDatabase.getInstance().addToFavourites(product);
        } else {
            ProductDatabase.getInstance().removeFromFavourites(product);
        }

        String buttonText = isAdding ? "Unlike" : "Like";
        toggleFavouriteStatusButton.setText(buttonText);
    }

    public void setOnShowMore(EventHandler<ActionEvent> handler) {
        showMoreButton.setOnAction(handler);
    }
}
