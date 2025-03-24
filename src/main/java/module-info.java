module com.project.shoppingrecommendationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;
    requires org.jsoup;
    requires org.seleniumhq.selenium.chrome_driver;
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
}