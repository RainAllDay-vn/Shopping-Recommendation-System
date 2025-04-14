package com.project.shoppingrecommendationsystem.models.chatbots;

public abstract class ChatBot {
    public static String prompt(String message){
        return "Answer";
    }
    public static void registerAction(String name,String functionContext,ChatBotCallable action){

    }
    public abstract String onPrompt(String message);
    public abstract void onRegisterAction(String name,String functionContext,ChatBotCallable action);
}
