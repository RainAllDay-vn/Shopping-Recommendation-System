package com.project.shoppingrecommendationsystem.views;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class MainPage {
    private Stage primaryStage;
    @FXML
    private BorderPane rootPane;

    public MainPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeUI();
    }

    private void initializeUI() {
        try {
            // Load root layout
            rootPane = new BorderPane();

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
            rootPane.getStylesheets().add(getClass().getResource
                    ("/com/project/shoppingrecommendationsystem/styling.css").toExternalForm());

            // Load product top bar
            rootPane.setTop(FXMLLoader.load(Objects.requireNonNull(getClass().getResource
                    ("/com/project/shoppingrecommendationsystem/components/top-bar.fxml"))));

            // Load product grid
            rootPane.setCenter(FXMLLoader.load(Objects.requireNonNull(getClass().getResource
                    ("/com/project/shoppingrecommendationsystem/components/product-grid.fxml"))));

            // Load chat box
            rootPane.setRight(FXMLLoader.load(Objects.requireNonNull(getClass().getResource
                    ("/com/project/shoppingrecommendationsystem/components/chat-box.fxml"))));

            // Load filter
            rootPane.setLeft(FXMLLoader.load(Objects.requireNonNull(getClass().getResource
                    ("/com/project/shoppingrecommendationsystem/components/filter-bar.fxml"))));

            // Load footer
            rootPane.setBottom(FXMLLoader.load(Objects.requireNonNull(getClass().getResource
                    ("/com/project/shoppingrecommendationsystem/components/footer-bar.fxml"))));

            // Set the scene and show
            Scene scene = new Scene(rootPane);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Shopping Recommendation System");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
