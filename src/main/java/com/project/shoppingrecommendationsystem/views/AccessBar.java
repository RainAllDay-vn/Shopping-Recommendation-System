package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.AccessBarController;

public class AccessBar extends View<AccessBarController> {
    public AccessBar() {
        root = load("components/access-bar.fxml", new AccessBarController());
    }
}
