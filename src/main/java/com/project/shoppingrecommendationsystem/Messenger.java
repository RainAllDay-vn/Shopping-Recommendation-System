package com.project.shoppingrecommendationsystem;

import com.project.shoppingrecommendationsystem.controllers.MainPageController;
import com.project.shoppingrecommendationsystem.models.chatbots.ChatBot;
import com.project.shoppingrecommendationsystem.models.chatbots.Gemini;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Messenger {
    private MainPageController mainPageController;
    public static Messenger instance = new Messenger();
    private final ObservableList<String[]> query = FXCollections.observableArrayList();
    private final ChatBot chatBot = new Gemini();

    private Messenger() {}

    public void setMainPageController(MainPageController mainPageController) {
        this.mainPageController = mainPageController;
    }

    public static Messenger getInstance() {
        return instance;
    }

    public ObservableList<String[]> getQuery() {
        return query;
    }

    public MainPageController getMainPageController() {
        return mainPageController;
    }

    public ChatBot getChatBot() {
        return chatBot;
    }
}
