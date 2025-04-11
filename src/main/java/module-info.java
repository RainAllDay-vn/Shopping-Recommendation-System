module com.project.shoppingrecommendationsystem {
    exports com.project.shoppingrecommendationsystem;
    exports com.project.shoppingrecommendationsystem.models;
    exports com.project.shoppingrecommendationsystem.models.components;
    exports com.project.shoppingrecommendationsystem.views;
    exports com.project.shoppingrecommendationsystem.controllers;

    requires com.fasterxml.jackson.databind;
    requires com.opencsv;
    requires java.sql;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.seleniumhq.selenium.chrome_driver;
<<<<<<< HEAD
    requires com.fasterxml.jackson.databind;
    requires org.checkerframework.checker.qual;
    requires java.desktop;
    requires jdk.compiler;


    opens com.project.shoppingrecommendationsystem to javafx.fxml;
    exports com.project.shoppingrecommendationsystem;
    exports com.project.shoppingrecommendationsystem.controllers;
    exports com.project.shoppingrecommendationsystem.models;
    exports com.project.shoppingrecommendationsystem.views;

    opens com.project.shoppingrecommendationsystem.controllers to javafx.fxml;
    opens com.project.shoppingrecommendationsystem.views to javafx.fxml;
=======
    requires org.seleniumhq.selenium.chromium_driver;
    requires org.jsoup;
    requires org.seleniumhq.selenium.firefox_driver;
    requires java.desktop;

    opens com.project.shoppingrecommendationsystem to javafx.fxml;
    opens com.project.shoppingrecommendationsystem.controllers to javafx.fxml;
>>>>>>> hieu/4-design-homepage
}