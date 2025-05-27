package com.project.shoppingrecommendationsystem.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.project.shoppingrecommendationsystem.llmagent.QuestionAdviser;
import com.project.shoppingrecommendationsystem.llmagent.conversationmodel.ConversationModel;
import com.project.shoppingrecommendationsystem.llmagent.conversationmodel.VertexConversationModel;
import com.project.shoppingrecommendationsystem.llmagent.embedmodel.VertexEmbedModel;
import com.project.shoppingrecommendationsystem.llmagent.vectordatabase.QdrantVectorDatabase;
import com.project.shoppingrecommendationsystem.llmagent.vectordatabase.VectorDatabase;
import javafx.fxml.Initializable;

import com.project.shoppingrecommendationsystem.ShoppingApplication;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class ChatBoxController implements Initializable {

    @FXML
    private TextArea chatInput;
    @FXML
    private Button sendButton;
    @FXML
    private ListView<HBox> chatListView;

    private Image userImage;
    private Image botImage;

    String storeName = System.getenv("VERTEX_AI_GEMINI_STORE_NAME");
    VectorDatabase vectorDatabase = new QdrantVectorDatabase(storeName, new VertexEmbedModel());
    ConversationModel curConversationModel = new VertexConversationModel();
    QuestionAdviser adviser = new QuestionAdviser(vectorDatabase, curConversationModel);

    public ChatBoxController() throws IOException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        URL userImageURL = ShoppingApplication.class.getResource("images/user-icon.jpg");
        URL botImageURL = ShoppingApplication.class.getResource("images/chat-bot-icon.png");
        try {
            assert userImageURL != null;
            userImage = new Image(userImageURL.toExternalForm());
            assert botImageURL != null;
            botImage = new Image(botImageURL.toExternalForm());
        } catch (Exception e) {
            System.err.println("[ERROR] : Can not load user or bot chat icon.");
        }
        sendButton.setOnAction(event -> askChatBox(chatInput.getText()));
        chatInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                askChatBox(chatInput.getText());
            }});
        addUIChatBotMessage("Hi how can I help you?");
    }

    private void askChatBox(String message) {
        if (message == null || message.trim().isEmpty()) return;
        chatInput.clear();
        addUIUserMessage(message);

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return adviser.advise(message);
            }

            @Override
            protected void succeeded() {
                String botReply = getValue();
                Platform.runLater(() -> {
                    addUIChatBotMessage(botReply);
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Text errorText = new Text("[ERROR] Failed to get response.");
                    chatListView.getItems().add(new HBox(errorText));
                });
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void addUIChatBotMessage(String message) {
        HBox botMessageBox = new HBox(10);
        botMessageBox.setStyle("-fx-padding: 5;");
        botMessageBox.setPrefWidth(chatListView.getPrefWidth());
        botMessageBox.setMaxWidth(Double.MAX_VALUE);

        TextFlow formattedBotText = formatBotResponse(message);
        formattedBotText.setPrefWidth(180);

        HBox botBubble = new HBox(formattedBotText);
        botBubble.setStyle("-fx-background-color: #F1F0F0; -fx-background-radius: 10; -fx-padding: 8;");
        botBubble.setMaxWidth(200);

        botMessageBox.getChildren().addAll(createChatIcon(botImage), botBubble);
        chatListView.getItems().add(botMessageBox);
    }

    private void addUIUserMessage(String message) {
        HBox userMessageBox = new HBox(10);
        userMessageBox.setStyle("-fx-padding: 5;");
        userMessageBox.setPrefWidth(chatListView.getPrefWidth());
        userMessageBox.setMaxWidth(Double.MAX_VALUE);

        Text userText = new Text(message);
        userText.wrappingWidthProperty().set(180);

        HBox userBubble = new HBox(userText);
        userBubble.setStyle("-fx-background-color: #DCF8C6; -fx-background-radius: 10; -fx-padding: 8;");
        userBubble.setMaxWidth(200);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        userMessageBox.getChildren().addAll(spacer, userBubble, createChatIcon(userImage));
        chatListView.getItems().add(userMessageBox);
    }

    private ImageView createChatIcon(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        imageView.setPreserveRatio(true);

        Circle clip = new Circle(20, 20, 20);
        imageView.setClip(clip);

        return imageView;
    }

    private TextFlow formatBotResponse(String message) {
        TextFlow textFlow = new TextFlow();
        textFlow.setLineSpacing(4);

        Pattern pattern = Pattern.compile("(\\*\\*([^*]+)\\*\\*)|([^*]+)");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            Text text = null;

            if (matcher.group(1) != null) {
                text = new Text(matcher.group(2));
                text.setFont(Font.font("System", FontWeight.BOLD, 13));
            } else if (matcher.group(3) != null) {
                text = new Text(matcher.group(3));
                text.setFont(Font.font("System", FontWeight.NORMAL, 13));
            }

            if (text != null) {
                textFlow.getChildren().add(text);
            }
        }

        return textFlow;
    }
}
