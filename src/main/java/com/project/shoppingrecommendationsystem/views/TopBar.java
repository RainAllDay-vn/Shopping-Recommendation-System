package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.TopBarController;

public class TopBar extends View<TopBarController> {
    public TopBar() {
        root = load("components/top-bar.fxml", new TopBarController());
    }
}
