package com.project.shoppingrecommendationsystem.models.chatbots;

import org.json.JSONArray;
import org.json.JSONObject;

import com.project.shoppingrecommendationsystem.models.chatbots.GeminiUtils.FunctionResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Gemini extends ChatBot {

    // Free tier Gemini API key.
    private final String postUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyA8MNbRBXb4FgGMXkqJyF6gXugWyq-Ut60";
    // System prompt for context.
    private final String systemPrompt = "You are a chatbot for a laptop recommendation website. You have a number of tools that you can use to change the UI, use it to enhance user experience. Help users choose laptops and use registered tools when needed.";

    // This list stores conversation messages; each message is a JSONObject with keys "role" and "parts".

    private JSONArray contents = new JSONArray();
    private JSONArray tools = new JSONArray();
    private JSONArray functionDeclarations = new JSONArray();


    public List<Class<?>> supportedTypes = Arrays.asList(
            String.class, boolean.class, int.class, float.class
    );
    //entry using class_name.method
    Map<String, FunctionInfo> actionsMap = new HashMap<>();

    public static class FunctionInfo {
        Object instance;
        Method method;
        Class<?>[] paramType = {};
        public FunctionInfo(Object instance, Method method, Class<?>[] paramType) {
            this.instance = instance;
            this.method = method;
            this.paramType = paramType;
        }
        public Object getInstance() {
            return instance;
        }
        public Method getMethod() {
            return method;
        }
        public Class<?>[] getParamType(){
            return this.paramType;
        }
    }

    @Override
    public void onInit() {
        tools.put(new JSONObject().put("functionDeclarations",functionDeclarations));
        contents.put(new JSONObject().put("role","user").put("parts", new JSONArray().put(new JSONObject().put("text", systemPrompt))));
    }

    @Override
    public void onRegisterAction(Object instance, Method method,String[] paramDesc, String desc) {
        if (!supportedTypes.contains(method.getReturnType())) {
            System.out.println(method.getReturnType() + " as return type is not supported");
            return;
        }
        Class<?>[] params = method.getParameterTypes();
        for (Class<?> paramType : params) {
            if (!supportedTypes.contains(paramType)) {
                System.out.println(paramType + " as parameter type is not supported");
                return;
            }
        }
        String signature = instance.getClass().getSimpleName() + "." + method.getName();
        actionsMap.put(signature, new FunctionInfo(instance, method,params));
        JSONObject methodObj = GeminiUtils.methodToGeminiJSON(signature, desc, params, paramDesc);
        functionDeclarations.put(methodObj);
    }

    private GeminiUtils.FunctionResponse<?> handleFunction(JSONObject functionCall){
        contents.put(GeminiUtils.constructModelContent(new JSONObject().put("functionCall", functionCall)));
        System.out.println(functionCall);
        String name = functionCall.getString("name");
        FunctionInfo function = actionsMap.get(name);
        JSONObject args = functionCall.getJSONObject("args");
        Object[] argValues = new Object[function.getParamType().length];
        for(int i = 0; i < function.getParamType().length; i++){
            String paramName = "param" + (i + 1);
            if (function.getParamType().length == 1 && args.has("param1")) {
                paramName = "param1";
            } else {
                try {
                    JSONObject parameters = functionDeclarations.toList().stream()
                            .filter(o -> ((JSONObject) o).getString("name").equals(name))
                            .findFirst()
                            .map(o -> ((JSONObject) o).getJSONObject("parameters"))
                            .orElse(new JSONObject());
                    JSONObject properties = parameters.optJSONObject("properties");
                    if (properties != null && properties.length() > 0) {
                        paramName = properties.keys().next();
                        if (args.has("param" + (i + 1))) {
                            paramName = "param" + (i + 1);
                        } else if (properties.length() == 1) {
                            paramName = properties.keys().next();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Class<?> paramType = function.getParamType()[i];
            if(paramType.equals(String.class)){
                argValues[i] = args.getString(paramName);
            } else if (paramType.equals(int.class)) {
                argValues[i] = args.getInt(paramName);
            } else if (paramType.equals(boolean.class)) {
                argValues[i] = args.getBoolean(paramName);
            } else if (paramType.equals(float.class)) {
                argValues[i] = (float) args.getDouble(paramName);
            }
        }

        Object result = null;
        try {
            function.getMethod().setAccessible(true);
            result = function.getMethod().invoke(function.getInstance(), argValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new FunctionResponse<>(name, "Error: Illegal Access");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return new FunctionResponse<>(name, "Error: Invocation Target Exception - " + e.getTargetException().getMessage());
        }

        return new FunctionResponse<>(name, result);
    }


    @Override
    public String onPrompt(String message) {
        String retText = null;
        List<FunctionResponse<?>> functionResponses = new LinkedList<>();
        JSONObject userMsg = GeminiUtils.constructUSerContent(message, functionResponses.toArray(new FunctionResponse[0]));
        contents.put(userMsg);
        JSONObject payload = GeminiUtils.constructPayload(tools, contents);
        System.out.println(payload);
        JSONObject response = new JSONObject(sendPostRequest(postUrl,payload.toString()));
        System.out.println(response);
        JSONArray candidates = response.getJSONArray("candidates");
        boolean containFunction = false;
        List<FunctionResponse<?>> currentResponses = new LinkedList<>();
        for(int i = 0; i< candidates.length(); i++){
            JSONObject candidate = candidates.getJSONObject(i);
            JSONObject content = candidate.optJSONObject("content");
            if (content != null) {
                JSONArray parts = content.getJSONArray("parts");
                for (int k = 0; k < parts.length(); k++) {
                    JSONObject part = parts.getJSONObject(k);
                    if (part.has("functionCall")) {
                        JSONObject funcCall = part.getJSONObject("functionCall");
                        currentResponses.add(handleFunction(funcCall));
                        containFunction = true;
                    }
                    if (part.has("text")) {
                        retText = part.getString("text");
                    }
                }
            }
        }

        if(containFunction){
            //Construct return
            // For now, we'll just return the text if available, or a message about function execution.
            if (retText == null && !currentResponses.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Function(s) executed:");
                for (FunctionResponse<?> res : currentResponses) {
                    sb.append("\n").append(res.toJSON().toString(2));
                }
                return sb.toString();
            }
        }
        return retText;
    }
}