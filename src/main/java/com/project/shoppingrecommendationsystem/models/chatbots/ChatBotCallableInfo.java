package com.project.shoppingrecommendationsystem.models.chatbots;

import java.util.List;

public interface ChatBotCallableInfo {
    public List<ChatBotParameter> getParameters();
    public String getDescription();
    public String getName();
}
