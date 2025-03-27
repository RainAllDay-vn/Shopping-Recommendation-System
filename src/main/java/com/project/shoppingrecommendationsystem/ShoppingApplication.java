package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.models.ProductDatabase;
import com.project.shoppingrecommendationsystem.views.MainPage;
import com.project.shoppingrecommendationsystem.views.ProductGrid;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ShoppingApplication extends Application {
    private ProductDatabase productDatabase;

    @Override
    public void start(Stage stage) throws IOException {
        productDatabase = ProductDatabase.getInstance();
        productDatabase.crawl(4);

        stage.setTitle("Shopping Recommendation System");
        stage.setFullScreen(false);

        stage.setScene(new MainPage().getScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}