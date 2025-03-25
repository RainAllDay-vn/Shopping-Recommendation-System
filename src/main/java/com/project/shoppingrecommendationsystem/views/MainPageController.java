package com.project.shoppingrecommendationsystem.views;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Objects;



public class MainPageController{
    @FXML
    private BorderPane rootPane;
    @FXML
    private ScrollPane productGrid;
    @FXML
    public void initialize(){
        initializeUI();
    }


    private void initializeUI() {
        try {
            // Load root layout

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
            productGrid.setContent(FXMLLoader.load(Objects.requireNonNull(getClass().getResource
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
