package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.MainPageController;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class MainPage extends View<MainPageController> {
    public MainPage() {
        root = load("main-page.fxml", new MainPageController());
    }

    @Override
    public Scene getScene() {
        return new Scene((Parent) root);
    }
}
