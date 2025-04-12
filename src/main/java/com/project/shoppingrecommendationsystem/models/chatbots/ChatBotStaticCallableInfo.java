package com.project.shoppingrecommendationsystem.models.chatbots;

import java.util.Arrays;
import java.util.List;

public class ChatBotStaticCallableInfo implements ChatBotCallableInfo {

    String name;
    String desc;
    ChatBotParameter[] params;

    public ChatBotStaticCallableInfo(String name, String desc, ChatBotParameter...params){
        this.name = name;
        this.desc = desc;
        this.params = params;
    }

    @Override
    public List<ChatBotParameter> getParameters() {
        return Arrays.asList(params);
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public String getName() {
        return name;
    }
    
}
