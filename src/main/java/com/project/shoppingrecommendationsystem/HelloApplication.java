package com.project.shoppingrecommendationsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.project.shoppingrecommendationsystem.models.CellphoneSCrawler;
import com.project.shoppingrecommendationsystem.models.Crawler;
import com.project.shoppingrecommendationsystem.models.FPTShopCrawler;
import com.project.shoppingrecommendationsystem.models.TGDDCrawler;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Crawler testCrawler = new CellphoneSCrawler();
        testCrawler.crawl();
        launch();
    }
}