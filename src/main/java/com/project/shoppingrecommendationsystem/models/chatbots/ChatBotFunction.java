package com.project.shoppingrecommendationsystem.models.chatbots;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ChatBotFunction {
    String desc();
    String[] paramDesc();
}
