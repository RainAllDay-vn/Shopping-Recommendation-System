module com.project.shoppingrecommendationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;
    requires org.jsoup;
    requires org.seleniumhq.selenium.chrome_driver;
    requires com.fasterxml.jackson.databind;

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
    exports com.project.shoppingrecommendationsystem;
    exports com.project.shoppingrecommendationsystem.llmagent;
    opens com.project.shoppingrecommendationsystem.llmagent to javafx.fxml;
}