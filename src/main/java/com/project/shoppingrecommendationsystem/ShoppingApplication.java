package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.controllers.MainPage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ShoppingApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {


//        #Check device screen size
//        int width = (int) Screen.getPrimary().getBounds().getWidth();
//        int height = (int) Screen.getPrimary().getBounds().getHeight();
//        System.out.print(width + " " + height);

        FXMLLoader fxmlLoader = new FXMLLoader(ShoppingApplication.class.getResource("main-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);


        stage.setTitle("Latptop Shopping Recommendation System");
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}