package com.project.shoppingrecommendationsystem.controllers;

import java.awt.*;
import java.net.URI;

public class FooterBarController {

    public void goToRepo(){
        String url = "https://github.com/RainAllDay-vn/Shopping-Recommendation-System/blob/4-design-a-homepage/src/main/java/com/project/shoppingrecommendationsystem/ShoppingApplication.java";
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
