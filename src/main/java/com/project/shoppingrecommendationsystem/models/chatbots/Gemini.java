package com.project.shoppingrecommendationsystem.models.chatbots;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.internal.Debug;

public class Gemini extends ChatBot {
    String postUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyA8MNbRBXb4FgGMXkqJyF6gXugWyq-Ut60";

    String chatContext = "you are a chat bot for a laptop recommendation website, do your best to recommend the customer, the user messages are wrapper in <user></user> tag, your previous messages are wrapped in <bot></bot> tag and your replies are automatically wrapped in <bot></bot> so no need to add it in your reply you can also call functions from the <functions></functions> tag, all of them accepts only one string as parameter and to call them you must use <func=\"function_name\" parameter = \"parameter\" >";
    String chatHistory = "";
    String functionCanCall = "";
    Map<String,ChatBotCallables> functions = new HashMap<>();

    private String sendPostRequest(String urlString, String payload) {
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();

        try {
            // Create URL object
            URL url = new URI(urlString).toURL();

            // Open a connection to the URL
            connection = (HttpURLConnection) url.openConnection();

            // Set the HTTP method to POST
            connection.setRequestMethod("POST");

            // Set headers
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // Enable input/output streams
            connection.setDoOutput(true);

            // Write the payload to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the response
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred during request: " + e.getMessage(); // Handle any exceptions
        } finally {
            if (connection != null) {
                connection.disconnect(); // Close the connection
            }
        }

        return response.toString(); // Return the response as a string
    }

    private String parseBotResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray candidates = jsonResponse.getJSONArray("candidates");

            // Check if there's at least one candidate
            if (candidates.length() > 0) {
                JSONObject firstCandidate = candidates.getJSONObject(0);
                JSONObject content = firstCandidate.getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");

                // Check if there's at least one part in the response
                if (parts.length() > 0) {
                    return parse(parts.getJSONObject(0).getString("text")); // Return the text from the first part)
                }
            }

            return "No valid response received.";
        } catch (Exception e) {
            e.printStackTrace();
            return null; // If anything goes wrong, return null
        }
    }
    private String parse(String botRes){
        System.out.println(botRes);
        String funcPattern = "<func=\"(.*?)\" parameter=\"(.*?)\" >";

        // List to hold function name and parameter arrays
        List<String[]> parsedFunctions = new ArrayList<>();

        // Extracting <func> calls using regex
        Pattern funcRegex = Pattern.compile(funcPattern);
        Matcher funcMatcher = funcRegex.matcher(botRes);
        
        // Extract function calls and remove them from the chat
        String cleanedChatText = botRes;
        while (funcMatcher.find()) {
            // Extract function name and parameter
            String functionName = funcMatcher.group(1);
            String functionParameter = funcMatcher.group(2);
            
            // Store parsed function details in a String array
            String[] functionDetails = {functionName, functionParameter};
            parsedFunctions.add(functionDetails);
            
            // Remove the <func> tags from the text
            cleanedChatText = cleanedChatText.replace(funcMatcher.group(0), "");
        }

        // Output the parsed function calls as arrays
        if (parsedFunctions.isEmpty()) {
            System.out.println("No function calls found.");
        } else {
            System.out.println("Parsed Function Calls:");
            parsedFunctions.forEach(function -> 
                System.out.println("Function Name: " + function[0] + ", Parameter: " + function[1])
            );
            for(String[] func : parsedFunctions){
                ChatBotCallables function =functions.get(func[0]);
                if(function == null){
                    System.out.println(func[0] + " does not exist");
                } 
                function.call(func[1]);
            }
        }

        // Output the cleaned chat text (with <func> tags removed)
        System.out.println("\nCleaned Chat (without function calls):");
        return cleanedChatText.trim();
    }

    @Override
    public void onInit() {

    }

    @Override
    public String onPrompt(String message) {
        chatHistory += "<user>" + message + "</user>";
        final String context ="<context>" + chatContext + "</context><functions>"+functionCanCall + "</functions><chat history>" + chatHistory + "</chat history>";
        System.out.println(context);
        JSONObject payload = new JSONObject();
        JSONArray contentsArray = new JSONArray();
        JSONObject contentObject = new JSONObject();
        JSONArray partsArray = new JSONArray();
        partsArray.put(new JSONObject().put("text", context));
        contentObject.put("parts", partsArray);
        contentsArray.put(contentObject);
        payload.put("contents", contentsArray);
        String response = sendPostRequest(postUrl, payload.toString());
        String botReply = parseBotResponse(response);
        chatHistory += "<bot>" + botReply + "</bot>";
        return botReply;
    }

    @Override
    public void onRegisterAction(String name,String functionContext, ChatBotCallables action) {
        functions.put(name, action);
        functionCanCall += "<name =" +name + ",description =" + functionContext + "/>,";
    }

}
