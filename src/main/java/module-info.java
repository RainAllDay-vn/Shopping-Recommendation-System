module com.project.shoppingrecommendationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;
    requires org.jsoup;
    requires org.seleniumhq.selenium.chrome_driver;
    requires com.fasterxml.jackson.databind;
    requires org.checkerframework.checker.qual;
    requires java.desktop;


    opens com.project.shoppingrecommendationsystem to javafx.fxml;
    exports com.project.shoppingrecommendationsystem;
    exports com.project.shoppingrecommendationsystem.controller;
    opens com.project.shoppingrecommendationsystem.controller to javafx.fxml;
}