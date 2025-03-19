module com.project.shoppingrecommendationsystem {
    exports com.project.shoppingrecommendationsystem;

    requires com.opencsv;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.seleniumhq.selenium.api;
    requires org.seleniumhq.selenium.chrome_driver;
    requires org.seleniumhq.selenium.chromium_driver;
    requires org.seleniumhq.selenium.remote_driver;
    requires org.jsoup;

    opens com.project.shoppingrecommendationsystem to javafx.fxml;
}
