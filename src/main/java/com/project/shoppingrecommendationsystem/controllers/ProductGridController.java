package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.views.RenderProductGrid;

public class ProductGridController {
    public static void expandProducts(RenderProductGrid grid) {
        grid.loadMoreProducts();
    }
}