package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.views.MainPage;
import com.project.shoppingrecommendationsystem.views.Overlay;
import com.project.shoppingrecommendationsystem.views.Overlay;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ShoppingApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        StackPane root = new StackPane();
        root.setPrefSize(1500, 800);
        root.getChildren().add(new MainPage().getRoot());
        root.getChildren().add(new Overlay().getRoot());
        StackPane root = new StackPane();
        root.setPrefSize(1500, 800);
        root.getChildren().add(new MainPage().getRoot());
        root.getChildren().add(new Overlay().getRoot());
        stage.setTitle("Shopping Recommendation System");
        stage.setFullScreen(false);
        stage.setScene(new Scene(root));
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}