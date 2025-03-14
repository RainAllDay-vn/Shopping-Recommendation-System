module com.project.shoppingrecommendationsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.project.shoppingrecommendationsystem to javafx.fxml;
    exports com.project.shoppingrecommendationsystem;
}