module com.project.shoppingrecommendationsystem {
    exports com.project.shoppingrecommendationsystem;
    exports com.project.shoppingrecommendationsystem.models;
    exports com.project.shoppingrecommendationsystem.models.components;
    exports com.project.shoppingrecommendationsystem.views;
    exports com.project.shoppingrecommendationsystem.controllers;
    exports com.project.shoppingrecommendationsystem.llmagent;

    requires com.fasterxml.jackson.databind;
    requires com.opencsv;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.seleniumhq.selenium.chrome_driver;
    requires org.seleniumhq.selenium.chromium_driver;
    requires org.jsoup;
    requires org.seleniumhq.selenium.firefox_driver;

    // Spring AI dependencies
    requires spring.ai.vertex.ai.gemini;
    requires spring.core;
    requires spring.ai.client.chat;
    requires spring.ai.commons;
    requires spring.ai.core;
    requires spring.ai.model;
    requires spring.ai.advisors.vector.store;
    requires spring.ai.qdrant.store;
    requires spring.ai.vertex.ai.embedding;
    requires spring.beans;
    requires spring.ai.template.st;
    requires spring.context;

    // Enable Vertex AI Gemini and Vertex AI embedding
    requires org.slf4j;
    requires gax;
    requires google.cloud.aiplatform;
    requires com.google.gson;
    requires com.google.api.apicommon;
    requires com.google.auth;
    requires com.google.auth.oauth2;
    requires com.google.common;
    requires google.cloud.vertexai;

    //qdrant client
    requires client;

    opens com.project.shoppingrecommendationsystem to javafx.fxml;
    opens com.project.shoppingrecommendationsystem.controllers to javafx.fxml;
    opens com.project.shoppingrecommendationsystem.llmagent to javafx.fxml;

}