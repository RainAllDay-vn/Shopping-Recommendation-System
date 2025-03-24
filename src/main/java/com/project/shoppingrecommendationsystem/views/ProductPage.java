package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.NavigationController;
import com.sun.tools.javac.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ProductPage {
    private final Stage productStage;
    @FXML
    private BorderPane productPagePane;

    public ProductPage(Stage productStage) {
        this.productStage = productStage;
        initializeUI();
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
            backButton.setOnAction(event -> NavigationController.goBack());
            productPagePane.setLeft(backButton);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Parent getRootAsParent() {
        return productPagePane;
    }

}

