package com.project.shoppingrecommendationsystem.controllers;

<<<<<<< HEAD
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ChatBoxController {

    @FXML
    private VBox chatContainer;

    @FXML
    private ListView<String> chatListView;

    @FXML
    private TextField chatInput;

    @FXML
    private Button sendButton;

    @FXML
    private Button chatToggleButton;

    @FXML
=======
import com.project.shoppingrecommendationsystem.ShoppingApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ChatBoxController {

    @FXML private VBox chatContainer;
    @FXML private ListView<Message> chatListView;
    @FXML private TextField chatInput;
    @FXML private ImageView chatBotLogo;
    @FXML private Button sendButton;

    private final Image userImage = new Image(ShoppingApplication.class.getResource("images/user-icon.jpg").toExternalForm());
    private final Image botImage = new Image(ShoppingApplication.class.getResource("images/chat-bot-icon.png").toExternalForm());

    public static class Message {
        private final String text;
        private final String sender;

        public Message(String text, String sender) {
            this.text = text;
            this.sender = sender;
        }

        public String getText() { return text; }
        public String getSender() { return sender; }
    }

    @FXML
    public void initialize() {
        chatBotLogo.setOnMouseClicked(event -> {toggleChat();});
        chatInput.setOnAction(event -> sendMessage());
        sendButton.setOnAction(event -> sendMessage());
        chatBotLogo.setImage(botImage);
        chatListView.setCellFactory(lv -> new MessageCell());
    }

>>>>>>> hieu/4-design-homepage
    public void toggleChat() {
        chatContainer.setVisible(!chatContainer.isVisible());
    }

<<<<<<< HEAD
    @FXML
    public void sendMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            chatListView.getItems().add(message);
            chatInput.clear();
        }
    }
}
=======
    public void sendMessage() {
        String messageText = chatInput.getText().trim();
        if (!messageText.isEmpty()) {
            chatListView.getItems().add(new Message(messageText, "user"));
            chatInput.clear();
            chatListView.getItems().add(new Message("This is a bot response", "bot"));
        }
    }

    private class MessageCell extends ListCell<Message> {
        private final HBox container = new HBox();
        private final ImageView avatar = new ImageView();
        private final Text messageText = new Text();

        public MessageCell() {
            container.setSpacing(10);
            avatar.setFitHeight(30);
            avatar.setFitWidth(30);
            avatar.setPreserveRatio(true);

            messageText.wrappingWidthProperty().bind(
                    chatListView.widthProperty().subtract(60)
            );
        }

        @Override
        protected void updateItem(Message message, boolean empty) {
            super.updateItem(message, empty);

            if (empty || message == null) {
                setGraphic(null);
            } else {
                if ("user".equals(message.getSender())) {
                    avatar.setImage(userImage);
                    container.getChildren().setAll(messageText, avatar);
                    container.setStyle("-fx-alignment: center-right;");
                } else {
                    avatar.setImage(botImage);
                    container.getChildren().setAll(avatar, messageText);
                    container.setStyle("-fx-alignment: center-left;");
                }

                messageText.setText(message.getText());
                setGraphic(container);
            }
        }
    }
}
>>>>>>> hieu/4-design-homepage
