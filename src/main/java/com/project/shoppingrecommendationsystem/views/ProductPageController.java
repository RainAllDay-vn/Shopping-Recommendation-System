package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.NavigationController;
import com.project.shoppingrecommendationsystem.models.Product;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Objects;

public class ProductPageController {
    @FXML
    private BorderPane productPagePane;

    @FXML
    public void initialize(){
        initializeUI();
    }

    public void setProduct(Product productData){
        
    }

    private void initializeUI() {
        try {
            productPagePane = new BorderPane();

//            // Checking file path
//            String fxml = "";
//            URL resource = getClass().getResource(fxml + ".fxml");
//
//            if (resource == null) {
//                System.out.println("FXML file not found: " + fxml + ".fxml");
//                return; // Stop execution if FXML is missing
//            } else {
//                System.out.println("FXML file found, proceeding to load...");
//            }

            // Load CSS
            productPagePane.getStylesheets().add(getClass().getResource
                    ("/com/project/shoppingrecommendationsystem/styling.css").toExternalForm());

            // Load product top bar
            productPagePane.setTop(FXMLLoader.load(Objects.requireNonNull(getClass().getResource
                    ("/com/project/shoppingrecommendationsystem/components/top-bar.fxml"))));

            // Load chat box
            productPagePane.setRight(FXMLLoader.load(Objects.requireNonNull(getClass().getResource
                    ("/com/project/shoppingrecommendationsystem/components/chat-box.fxml"))));

                    // Load footer
            productPagePane.setBottom(FXMLLoader.load(Objects.requireNonNull(getClass().getResource
                    ("/com/project/shoppingrecommendationsystem/components/footer-bar.fxml"))));

            // Load temporary button that come back to main scene
            Button backButton = new Button("Back");
            backButton.setOnAction(event -> NavigationController.pop());
            productPagePane.setLeft(backButton);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

