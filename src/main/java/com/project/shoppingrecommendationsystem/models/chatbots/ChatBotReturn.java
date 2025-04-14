package com.project.shoppingrecommendationsystem.models.chatbots;

import org.json.JSONObject;

public interface ChatBotReturn {
    public Class<?> getType();
    public Object getValue();
    public JSONObject toJSON();
}
