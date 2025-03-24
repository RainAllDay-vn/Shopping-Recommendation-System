package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.models.Product;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class NavigationController {

    public static Parent mainPage;
    public static Parent productPage;
    public static Stack<Parent> sceneStack = new Stack<>();
    public static Stage stage;
    public static Scene scene;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public static void goToProduct(Product product) {

            // Store current scene before navigating
            sceneStack.push(stage.getScene().getRoot());
            // Set new scene
            stage.getScene().setRoot(productPage);
    }


    public static void goBack() {
        if (!sceneStack.isEmpty()) {
            stage.getScene().setRoot(sceneStack.pop());
        }
    }
}
