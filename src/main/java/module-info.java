module com.project.shoppingrecommendationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;
    requires org.jsoup;
    requires org.seleniumhq.selenium.chrome_driver;
    requires com.fasterxml.jackson.databind;
    requires org.checkerframework.checker.qual;


    opens com.project.shoppingrecommendationsystem to javafx.fxml;
    exports com.project.shoppingrecommendationsystem;
}