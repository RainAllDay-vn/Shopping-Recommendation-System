package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.controllers.NavigationController;
import com.project.shoppingrecommendationsystem.views.MainPage;
import com.project.shoppingrecommendationsystem.views.ProductPage;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ShoppingApplication extends Application {
    @Override
    public void start(Stage stage) {
        // Get device size
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        System.out.println("Screen Width: " + screenWidth);
        System.out.println("Screen Height: " + screenHeight);

        try {
            NavigationController.mainPage = (new MainPage(stage).getRootAsParent()); // Load MainPage class
            NavigationController.productPage = (new ProductPage(stage).getRootAsParent());
            Scene scene = new Scene(NavigationController.mainPage,screenWidth*0.8,screenHeight*0.8);
            scene.setOnMouseClicked(event -> scene.getRoot().requestFocus());

            NavigationController.scene = scene;
            NavigationController.stage = stage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        stage.setScene(NavigationController.scene);
        stage.setTitle("Shopping Recommendation System");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
