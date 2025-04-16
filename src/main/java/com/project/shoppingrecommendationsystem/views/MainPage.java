package com.project.shoppingrecommendationsystem.views;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class MainPage extends View {
    public MainPage() {
        root = load("main-page.fxml");
    }

    @Override
    public Scene getScene() {
        return new Scene((Parent) root);
    }
}
