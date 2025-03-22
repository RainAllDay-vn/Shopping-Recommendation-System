package com.project.shoppingrecommendationsystem.views;

import com.project.shoppingrecommendationsystem.controllers.ChatBoxController;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class RenderChatBox extends VBox {

    private VBox chatContainer;
    private ListView<String> chatListView;
    private TextField chatInput;
    private Button sendButton;
    private Button chatToggleButton;
    private ChatBoxController chatBoxController;

    public RenderChatBox() {
        // Initialize controller
        chatBoxController = new ChatBoxController(this);

        // Chat box container
        chatContainer = new VBox();
        chatContainer.setPrefSize(302, 720);
        chatContainer.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-padding: 10;");
        chatContainer.setVisible(false); // Initially hidden

        // Chat List
        chatListView = new ListView<>();
        chatListView.setPrefSize(302, 555);

        // Input area
        HBox inputBox = new HBox();
        inputBox.setPrefSize(200, 100);

        chatInput = new TextField();
        chatInput.setPrefSize(236, 91);

        sendButton = new Button("Send");
        sendButton.setPrefSize(66, 53);

        inputBox.getChildren().addAll(chatInput, sendButton);
        chatContainer.getChildren().addAll(chatListView, inputBox);

        // Circle Button (Chat Toggle)
        Circle chatIcon = new Circle(31, Color.DODGERBLUE);
        chatIcon.setStroke(Color.BLACK);

        chatToggleButton = new Button();
        chatToggleButton.setGraphic(chatIcon);
        chatToggleButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        // Assign event handlers to the controller
        chatToggleButton.setOnAction(e -> chatBoxController.toggleChat());
        sendButton.setOnAction(e -> chatBoxController.sendMessage());
        chatInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                chatBoxController.sendMessage();
            }
        });

        // Add components to layout
        this.getChildren().addAll(chatContainer, chatToggleButton);
    }

    // Getters for controller access
    public VBox getChatContainer() {
        return chatContainer;
    }

    public ListView<String> getChatListView() {
        return chatListView;
    }

    public TextField getChatInput() {
        return chatInput;
    }

    public void addMessage(String message) {
        chatListView.getItems().add(message);
    }
}
