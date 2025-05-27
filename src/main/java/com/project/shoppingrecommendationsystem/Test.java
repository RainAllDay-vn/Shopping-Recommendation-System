package com.project.shoppingrecommendationsystem; // Adjust package as needed

import com.project.shoppingrecommendationsystem.controllers.CrawlOptionsPopupController;
import com.project.shoppingrecommendationsystem.models.database.ListDatabase;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Test extends Application { // Assuming this is your main Application class

    private ListDatabase database; // Your database instance

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize your database first, potentially with existing data or a fresh crawl
        database = new ListDatabase(); // This constructor already performs an initial crawl

        // Show the pop-up before loading the main UI
        CrawlOptionsPopupController.CrawlOption choice = showCrawlOptionsPopup(primaryStage);

        switch (choice) {
            case PERFORM_CRAWL:
                System.out.println("Initiating crawl based on user selection...");
                // The controller provides selected crawlers, but ListDatabase.crawl()
                // currently takes all or a single crawler. You might need to adapt
                // ListDatabase.crawl() to take a List<Crawler> or iterate here.
                // For demonstration, let's assume you want to re-crawl all if chosen.
                // If you want to crawl only specific ones, you'd need to
                // database.crawl(selectedCrawlers) or a similar method.
                database.crawl(); // Perform a full crawl or use selected crawlers from popupController.getSelectedCrawlers()
                loadMainApplication(primaryStage);
                break;
            case SKIP_CRAWL:
                System.out.println("Skipping crawl. Using existing data (if any).");
                loadMainApplication(primaryStage);
                break;
            case EXIT_APP:
                System.out.println("User chose to exit the application.");
                Platform.exit(); // Exit the entire JavaFX application
                break;
            case NONE: // This case is handled by setOnCloseRequest in popup controller
                System.out.println("Pop-up closed without explicit choice. Assuming skip.");
                loadMainApplication(primaryStage);
                break;
        }
    }

    private CrawlOptionsPopupController.CrawlOption showCrawlOptionsPopup(Stage ownerStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/shoppingrecommendationsystem/components/crawl-options-pop-up.fxml"));
            Parent root = loader.load();
            CrawlOptionsPopupController controller = loader.getController();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Blocks input to other windows
            popupStage.initOwner(ownerStage); // Set the main window as owner
            popupStage.setTitle("Data Load Options");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);

            controller.setDialogStage(popupStage); // Pass stage to controller for closing

            popupStage.showAndWait(); // Show pop-up and wait for it to close

            return controller.getUserChoice(); // Return the user's choice
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback in case of pop-up loading error
            return CrawlOptionsPopupController.CrawlOption.SKIP_CRAWL;
        }
    }

    private void loadMainApplication(Stage primaryStage) throws IOException {
        // Load your main application UI here
        // Example:
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/project/shoppingrecommendationsystem/main-page.fxml"));
        // Assuming MainApplicationView.fxml has an associated controller
        Parent mainRoot = mainLoader.load();
        // You might want to pass the 'database' instance to your main controller
        // MainAppController mainAppController = mainLoader.getController();
        // mainAppController.setDatabase(database);

        primaryStage.setScene(new Scene(mainRoot));
        primaryStage.setTitle("Shopping Recommendation System");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}