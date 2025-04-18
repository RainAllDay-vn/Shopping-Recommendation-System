package com.project.shoppingrecommendationsystem.models.chatbots;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
public abstract class ChatBot {
    static ChatBot instance;
    public static ChatBot getInstance() throws Exception{
        if(instance == null){
            throw new Exception("Chatbot not innitialized");
        }
        return instance;
    }
    public static void Init(ChatBot chatbot){
        instance = chatbot;
        chatbot.onInit();
    }
    public static String prompt(String message){
        return instance.onPrompt(message);
    }
    public static void registerComponent(Object obj){
        if(instance == null){
            System.out.println("no chat bot instance");
            return;
        }
        Method[] methods = obj.getClass().getDeclaredMethods();
        for(Method method: methods){
            if(method.isAnnotationPresent(ChatBotFunction.class)){
                ChatBotFunction annotation = method.getAnnotation(ChatBotFunction.class);
                instance.onRegisterAction(obj, method,annotation.paramDesc(),annotation.desc());
            }
        }
    }
    protected static String sendPostRequest(String urlString, String payload) {
        System.out.println(payload);
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URI(urlString).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return response.toString();
    }
    public abstract void onInit();
    public abstract String onPrompt(String message);
    public abstract void onRegisterAction(Object instance, Method method,String[] paramDesc,String desc);
}
