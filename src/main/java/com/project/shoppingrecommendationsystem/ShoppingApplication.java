package com.project.shoppingrecommendationsystem;

<<<<<<< HEAD
=======
<<<<<<< HEAD
import com.project.shoppingrecommendationsystem.models.ProductDatabase;
import com.project.shoppingrecommendationsystem.models.chatbots.ChatBot;
import com.project.shoppingrecommendationsystem.models.chatbots.Gemini;
=======
>>>>>>> 3fb4f8371ec22213294e88dd2c87cd5ea4e8b321
>>>>>>> d828c32cb350deece54358bda09faf9f6a7b28c1
import com.project.shoppingrecommendationsystem.views.MainPage;
import com.project.shoppingrecommendationsystem.views.Overlay;
import javafx.application.Application;
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
        stage.setTitle("Shopping Recommendation System");
        stage.setFullScreen(false);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}