package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.FooterBarController;

public class FooterBar extends View<FooterBarController> {
    public FooterBar() {
        root = load("components/footer-bar.fxml", new FooterBarController());
    }
}
