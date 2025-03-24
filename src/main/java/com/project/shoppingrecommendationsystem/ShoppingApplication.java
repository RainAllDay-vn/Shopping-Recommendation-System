package com.project.shoppingrecommendationsystem;

import java.net.URL;

import com.project.shoppingrecommendationsystem.controllers.IPage;
import com.project.shoppingrecommendationsystem.controllers.NavigationController;
import com.project.shoppingrecommendationsystem.views.MainPageController;
import com.project.shoppingrecommendationsystem.views.ProductPageController;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
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
        mainPage = new IPage() {
            MainPageController controller;
            Parent view;
            @Override
            public void init() {
                URL path = ShoppingApplication.class.getResource("main-page.fxml");
                FXMLLoader loader = new FXMLLoader(path);
                controller = loader.getController();
                try {
                    view = loader.load();   
                } catch (Exception e) {
                    System.err.println(e);
                    
                }
            }
            @Override
            public Object getController() {
                return controller;
            }
            @Override
            public Parent getRootAsParent() {
                return view;
            }
        };
        mainPage.init();
        productPage = new IPage() {
            ProductPageController controller;
            Parent view;
            @Override
            public void init() {
                VBox vbox = new VBox();
                view = vbox;
                Button button = new Button("Back");
                button.setOnMouseClicked(new EventHandler<Event>() {
                    public void handle(Event event) {
                        NavigationController.pop();
                    };
                });
                vbox.getChildren().add(new Label("Hello"));
                vbox.getChildren().add(button);
                controller = new ProductPageController();
            }
            @Override
            public Object getController() {
                return controller;
            }
            @Override
            public Parent getRootAsParent() {
                return view;
            }
        };
        productPage.init();
        NavigationController.init(stage,mainPage);
        stage.setTitle("Shopping Recommendation System");
        stage.setResizable(false);
        stage.setFullScreen(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
