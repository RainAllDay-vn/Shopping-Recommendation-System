package com.project.shoppingrecommendationsystem.views;

import java.io.IOException;
import java.util.Objects;

import com.project.shoppingrecommendationsystem.controllers.IPage;
import com.project.shoppingrecommendationsystem.controllers.PageFromDisk;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

public class ProgramPages {
    public static IPage productGrid;
    public static IPage productPage;
    public static Node chatBox;
    public static Node header;
    public static Node footer;
    public static Node filterPanel;
    public static void init() throws IOException{
        productGrid = new PageFromDisk(ProgramPages.class.getResource
                    ("/com/project/shoppingrecommendationsystem/components/product-grid.fxml"));
        header =  FXMLLoader.load(Objects.requireNonNull(ProgramPages.class.getResource
                    ("/com/project/shoppingrecommendationsystem/components/top-bar.fxml")));
        footer = FXMLLoader.load(Objects.requireNonNull(ProgramPages.class.getResource
                    ("/com/project/shoppingrecommendationsystem/components/footer-bar.fxml")));
        chatBox = FXMLLoader.load(Objects.requireNonNull(ProgramPages.class.getResource
                    ("/com/project/shoppingrecommendationsystem/components/chat-box.fxml")));
        filterPanel = FXMLLoader.load(Objects.requireNonNull(ProgramPages.class.getResource
                    ("/com/project/shoppingrecommendationsystem/components/filter-bar.fxml")));
        productPage = new PageFromDisk(ProgramPages.class.getResource
                    ("/com/project/shoppingrecommendationsystem/product-page.fxml"));
    }
}
