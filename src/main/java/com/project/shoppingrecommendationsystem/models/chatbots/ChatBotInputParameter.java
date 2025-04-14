package com.project.shoppingrecommendationsystem.models.chatbots;

public interface ChatBotInputParameter {
    public String getName();
    public Class<?> getType();
    public Object getValue();
}
