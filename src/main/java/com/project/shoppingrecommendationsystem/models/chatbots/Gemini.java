package com.project.shoppingrecommendationsystem.models.chatbots;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gemini extends ChatBot {
    // A wild free tier gemini API key has appeared
    private final String postUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyA8MNbRBXb4FgGMXkqJyF6gXugWyq-Ut60";
    private final String systemPrompt = "You are a chatbot for a laptop recommendation website. Help users choose laptops and use registered tools when needed.";
    private final List<JSONObject> chatHistory = new ArrayList<>();
    private final Map<String, ChatBotCallable> registeredFunctions = new HashMap<>();
    private final JSONArray toolsArray = new JSONArray();
    private final JSONArray functionDeclarations = new JSONArray();

    public void onRegisterAction(String name, String desc, ChatBotCallable action) {
        registeredFunctions.put(name, action);
        JSONObject func = new JSONObject();
        ChatBotCallableInfo info = action.getInfo();

        // Build the function declaration.
        func.put("name", info.getName());
        func.put("description", info.getDescription());

        // Build the parameters schema.
        JSONObject parameters = new JSONObject();
        parameters.put("type", "object");
        JSONObject properties = new JSONObject();
        JSONArray required = new JSONArray();

        for (ChatBotParameter param : info.getParameters()) {
            JSONObject paramDef = new JSONObject();
            paramDef.put("description", param.getDescription());
            if (param.getType().equals(String.class)) {
                paramDef.put("type", "string");
            } else if (param.getType().equals(Integer.class) || param.getType().equals(int.class)) {
                paramDef.put("type", "integer");
            } else if (param.getType().equals(Boolean.class) || param.getType().equals(boolean.class)) {
                paramDef.put("type", "boolean");
            } else {
                paramDef.put("type", "string"); // fallback if type is not recognized
            }
            properties.put(param.getName(), paramDef);
            required.put(param.getName());
        }
        parameters.put("properties", properties);
        parameters.put("required", required);
        func.put("parameters", parameters);

        // Append the function declaration to our list.
        functionDeclarations.put(func);
    }

    @Override
    public String onPrompt(String message) {
        
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        JSONArray userParts = new JSONArray();
        userMsg.put("parts", userParts);
        // If the conversation is new, prepend the system prompt.
        if (chatHistory.isEmpty()) {
            userParts.put(new JSONObject().put("text", systemPrompt + " " + message));
        } else {
            userParts.put(new JSONObject().put("text", message));
        }

        return doPrompt(userMsg);
    }

    private String doPrompt(JSONObject userMsg){
        chatHistory.add(userMsg);
        JSONArray messages = new JSONArray();
        for (JSONObject msg : chatHistory) {
            messages.put(msg);
        }
        JSONObject payload = new JSONObject();
        payload.put("contents", messages);
        if (!functionDeclarations.isEmpty()) {
            payload.put("tools", toolsArray);
        }
        //System.out.println(payload.toString());
        String response = sendPostRequest(postUrl, payload.toString());
        return handleResponse(response);
    }

    private String sendPostRequest(String urlString, String payload) {
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
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response.toString();
    }

    
    private String handleResponse(String rawJson) {
        try {
            JSONObject json = new JSONObject(rawJson);
            JSONArray candidates = json.getJSONArray("candidates");
            if (candidates.length() > 0) {
                JSONObject candidate = candidates.getJSONObject(0);
                if (candidate.has("content")) {
                    JSONObject content = candidate.getJSONObject("content");
                    if (content.has("parts")) {
                        JSONArray parts = content.getJSONArray("parts");
                        if (parts.length() > 0) {
                            JSONObject firstPart = parts.getJSONObject(0);
                            // Check for a function call.
                            if (firstPart.has("functionCall")) {
                                JSONObject functionCall = firstPart.getJSONObject("functionCall");
                                return handleFunctionCall(functionCall);
                            } else if (firstPart.has("text")) {
                                String text = firstPart.getString("text");
                                addAssistantMessage(text,"text");
                                return text;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing response: " + e.getMessage();
        }
        return "No valid reply received.";
    }
    private String handleFunctionCall(JSONObject functionCall) {
        try {
            JSONObject callMsg = new JSONObject();
            callMsg.put("role", "model");
            callMsg.put("parts", new JSONArray().put(new JSONObject().put("functionCall", functionCall)));
            chatHistory.add(callMsg);
            String name = functionCall.getString("name");
            JSONObject args = functionCall.getJSONObject("args");
            ChatBotCallable callable = registeredFunctions.get(name);
            if (callable == null) {
                return "Error: Unregistered function " + name;
            }

            List<ChatBotInputParameter> inputList = new ArrayList<>();
            for (ChatBotParameter param : callable.getInfo().getParameters()) {
                Object rawValue = args.has(param.getName()) ? args.get(param.getName()) : null;
                Object value = castToType(rawValue, param.getType());
                ChatBotInputParameter inputParam = new ChatBotInputParameter() {
                    @Override
                    public String getName() {
                        return param.getName();
                    }

                    @Override
                    public Class<?> getType() {
                        return param.getType();
                    }

                    @Override
                    public Object getValue() {
                        return value;
                    }
                };
                inputList.add(inputParam);
            }

            ChatBotReturn result = callable.call(inputList.toArray(new ChatBotInputParameter[0]));

            // Record the function result in the conversation history.
            JSONObject functionMsg = new JSONObject();
            functionMsg.put("role", "user");
            JSONObject functionResPart = new JSONObject();
            functionResPart.put("name", name);
            functionResPart.put("response", result.toJSON());
            JSONArray parts = new JSONArray();
            parts.put(new JSONObject().put("functionResponse", functionResPart));
            functionMsg.put("parts", parts);
            return doPrompt(functionMsg);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in function call: " + e.getMessage();
        }
    }

    /**
     * Casts a raw JSON value to the specified Java type.
     */
    private Object castToType(Object value, Class<?> type) {
        if (value == null) return null;
        try {
            if (type.equals(String.class)) {
                return value.toString();
            } else if (type.equals(Integer.class) || type.equals(int.class)) {
                return Integer.parseInt(value.toString());
            } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                return Boolean.parseBoolean(value.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return value;
    }

    /**
     * Adds an assistant message to the conversation history.
     */
    private void addAssistantMessage(String text,String type) {
        JSONObject msg = new JSONObject();
        msg.put("role", "assistant");
        JSONArray parts = new JSONArray();
        parts.put(new JSONObject().put(type, text));
        msg.put("parts", parts);
        chatHistory.add(msg);
    }
}
