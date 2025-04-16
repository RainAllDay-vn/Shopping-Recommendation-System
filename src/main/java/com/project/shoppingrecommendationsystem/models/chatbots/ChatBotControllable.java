package com.project.shoppingrecommendationsystem.models.chatbots;

public class ChatBotControllable {
    protected ChatBotControllable(){
        ChatBot.registerComponent(this.getClass());
    }
}
