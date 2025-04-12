package com.project.shoppingrecommendationsystem.models.chatbots;




public interface ChatBotCallable {    
    public ChatBotReturn call(ChatBotInputParameter... params);
    public ChatBotCallableInfo getInfo();
}
