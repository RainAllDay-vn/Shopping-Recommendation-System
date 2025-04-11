package com.project.shoppingrecommendationsystem;

<<<<<<< HEAD
import java.io.IOException;

import com.project.shoppingrecommendationsystem.controllers.IPage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ShoppingApplication extends Application {
    public static IPage productPage;
    public static IPage mainPage;
    @Override
    public void start(Stage stage) {
        // Get device size
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        System.out.println("Screen Width: " + screenWidth);
        System.out.println("Screen Height: " + screenHeight);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("program-layout.fxml"));
        try{
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        }catch(IOException e){
            System.err.println(e);
        }
        stage.setTitle("Shopping Recommendation System");
        stage.setResizable(false);
        stage.setFullScreen(false);
=======
import com.project.shoppingrecommendationsystem.models.ProductDatabase;
import com.project.shoppingrecommendationsystem.views.MainPage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class ShoppingApplication extends Application {
    private ProductDatabase productDatabase;

    @Override
    public void start(Stage stage) throws IOException {
        productDatabase = ProductDatabase.getInstance();

        stage.setTitle("Shopping Recommendation System");
        stage.setFullScreen(false);

        stage.setScene(new MainPage().getScene());
>>>>>>> hieu/4-design-homepage
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> hieu/4-design-homepage
