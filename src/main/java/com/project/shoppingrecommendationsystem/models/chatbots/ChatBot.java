package com.project.shoppingrecommendationsystem.models.chatbots;

public abstract class ChatBot {
    static ChatBot instance;
    public static ChatBot getInstance() throws Exception{
        if(instance == null){
            throw new Exception("Chatbot not innitialized");
        }
        return instance;
    }
    public static void Init(ChatBot chatbot){
        instance = chatbot;
        chatbot.onInit();
    }
    public static String prompt(String message){
        return instance.onPrompt(message);
    }
    public static void registerAction(String name,String functionContext,ChatBotCallables action){
        if(instance == null){
            System.out.println("no chat bot instance");
            return;
        }
        instance.onRegisterAction(name,functionContext, action);
    }
    public abstract void onInit();
    public abstract String onPrompt(String message);
    public abstract void onRegisterAction(String name,String functionContext,ChatBotCallables action);
}
