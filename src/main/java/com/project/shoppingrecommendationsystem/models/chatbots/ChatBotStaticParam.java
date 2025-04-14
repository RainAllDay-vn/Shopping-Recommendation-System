package com.project.shoppingrecommendationsystem.models.chatbots;

public class ChatBotStaticParam implements ChatBotParameter {

    String desc;
    String name;
    Class<?> clazz;
    public ChatBotStaticParam(Class<?> clazz,String name, String desc){
        this.desc = desc;
        this.name = name;
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public Class<?> getType() {
        return clazz;
    }
}
