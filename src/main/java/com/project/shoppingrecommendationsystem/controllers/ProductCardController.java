package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.Messenger;
import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.models.Laptop;
import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.database.ListDatabase;
import com.project.shoppingrecommendationsystem.views.LaptopDetails;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class ProductCardController implements Initializable {
    private static final Image defaultImage = loadDefaultImage();
    private Product product;
    private Node root;

    @FXML
    private ImageView productImage;
    @FXML
    private Label productName;
    @FXML
    private Label productPrice;
    @FXML
    private Label productDiscount;
    @FXML
    private Button showMoreButton;
    @FXML
    private Button favouriteButton;

    public void setProduct(Product product) {
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
        productPrice.setText("Price: "+ product.getPrice());
        productDiscount.setText("Discount : "+ product.getDiscountPrice());
        boolean isFavorite = ListDatabase.getInstance().isFavourite(product);
        if (isFavorite) {
            favouriteButton.setText("Unlike");
        } else {
            favouriteButton.setText("Like");
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

    public void setRoot(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        favouriteButton.setOnAction(event -> toggleFavouriteStatus());
        showMoreButton.setOnAction(event -> {
            goToProductDetails(product);
            showMoreButton.setOnAction(e -> goToWebPage(product));
        });
    }

    private void toggleFavouriteStatus() {
        if (ListDatabase.getInstance().isFavourite(product)) {
            ListDatabase.getInstance().removeFromFavourites(product);
            favouriteButton.setText("Like");
        } else {
            ListDatabase.getInstance().addToFavourites(product);
            favouriteButton.setText("Unlike");
        }
    }

    public void goToProductDetails(Product product) {
        LaptopDetails laptopDetails = new LaptopDetails((Laptop) product, getRoot());
        Messenger.getInstance().getMainPageController().displayDetails(laptopDetails.getRoot());
    }

    public void goToWebPage(Product product) {
        String url = product.getSourceURL().replaceAll("\"", "");
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
