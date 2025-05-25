package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.ProductDatabase;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class FooterBarController implements Initializable {

    private final ProductDatabase productDatabase = ProductDatabase.getInstance();
    private final ObservableList<Product> compareProducts = FXCollections.observableList(productDatabase.getCompareList());

    @FXML
    Button footerLabel;
    @FXML
    HBox compareBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        footerLabel.setOnMouseClicked(event -> goToRepo());

        compareProducts.addListener((InvalidationListener) observable -> {
            compareBox.getChildren().clear();
            for(Product p : compareProducts) {
                compareBox.getChildren().add(new Button(p.getName()) {{
                    setOnAction(e -> {
                        productDatabase.removeFromCompareList(p);
                    });
                    System.out.println("Compare button clicked -- " + p.getName());
                }});
            }
        });
    }

    public void goToRepo(){
        String url = "https://github.com/RainAllDay-vn/Shopping-Recommendation-System";
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
