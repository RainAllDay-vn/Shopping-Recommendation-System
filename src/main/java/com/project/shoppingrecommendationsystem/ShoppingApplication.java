package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.views.MainPage;
import javafx.application.Application;
import javafx.stage.Stage;

public class ShoppingApplication extends Application {
    @Override
    public void start(Stage stage) {
        new MainPage(stage); // Load MainPage class
    }

    public static void main(String[] args) {
        launch(args);
    }
}
