package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.views.RenderProductCard;

public class ProductCardController {

     private RenderProductCard productCard;

     public ProductCardController(RenderProductCard productCard) {
          this.productCard = productCard;
     }

     public void goToProductPage (Product product) {

     }

}
