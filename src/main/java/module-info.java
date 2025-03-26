module com.project.shoppingrecommendationsystem {
    exports com.project.shoppingrecommendationsystem;
    exports com.project.shoppingrecommendationsystem.models;
    exports com.project.shoppingrecommendationsystem.models.components;

    requires com.fasterxml.jackson.databind;
    requires com.opencsv;
    requires java.sql;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.seleniumhq.selenium.chrome_driver;
    requires org.seleniumhq.selenium.chromium_driver;
    requires org.jsoup;
    requires org.seleniumhq.selenium.firefox_driver;

    opens com.project.shoppingrecommendationsystem to javafx.fxml;
}