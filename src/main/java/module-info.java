module com.project.shoppingrecommendationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;
    requires org.jsoup;
    requires org.seleniumhq.selenium.chrome_driver;
    requires com.fasterxml.jackson.databind;
    requires jsr305;


    opens com.project.shoppingrecommendationsystem to javafx.fxml;
    exports com.project.shoppingrecommendationsystem;
}