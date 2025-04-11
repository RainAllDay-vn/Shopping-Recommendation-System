package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.FilterBarController;

public class FilterBar extends View<FilterBarController> {
    public FilterBar() {
        root = load("components/filter-bar.fxml", new FilterBarController());
    }
}
