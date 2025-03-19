package com.project.shoppingrecommendationsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-page.fxml"));


//        #Check device screen size
//        int width = (int) Screen.getPrimary().getBounds().getWidth();
//        int height = (int) Screen.getPrimary().getBounds().getHeight();
//        System.out.print(width + " " + height);

        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("what the fuck is happening !");//Bruh
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}