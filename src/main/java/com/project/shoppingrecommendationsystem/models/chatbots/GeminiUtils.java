package com.project.shoppingrecommendationsystem.models.chatbots;

import org.json.JSONArray;
import org.json.JSONObject;

public class GeminiUtils {
    public static class FunctionResponse <T>{
        String name;
        T res;
        public FunctionResponse(String name,T res) {
            this.name = name;
            this.res = res;
        }
        public JSONObject toJSON(){
            return new JSONObject().put("name", name).put("response", new JSONObject().put("result", res));
        }
    }

    public static JSONObject methodToGeminiJSON(String methodName, String methodDesc, Class<?>[] paramClasses,String[] paramDesc) {
        // Create the function JSON object and add the basic properties.
        JSONObject functionJson = new JSONObject();
        functionJson.put("name", methodName);

        // Use a custom description if you want.
        String description = methodDesc;
        if ("schedule_meeting".equals(methodName)) {
            description = "Schedules a meeting with specified attendees at a given time and date.";
        }
        functionJson.put("description", description);

        // Prepare the "parameters" object
        JSONObject parameters = new JSONObject();
        parameters.put("type", "object");

        // Determine the parameter names.
        String[] paramNames = new String[paramClasses.length];
        if ("schedule_meeting".equals(methodName)) {
            // Hard-coded parameter names for schedule_meeting
            paramNames = new String[] { "attendees", "date", "time", "topic" };
        } else {
            // Default generic names (e.g., "param1", "param2", ...)
            for (int i = 0; i < paramClasses.length; i++) {
                paramNames[i] = "param" + (i + 1);
            }
        }

        // Build the properties object and the required array.
        JSONObject properties = new JSONObject();
        JSONArray required = new JSONArray();

        for (int i = 0; i < paramClasses.length; i++) {
            JSONObject paramJson = new JSONObject();
            Class<?> clazz = paramClasses[i];

            if (clazz.isArray()) {
                // If the parameter is an array, then set type "array" and determine the items
                // type.
                paramJson.put("type", "array");
                JSONObject items = new JSONObject();
                // Assume that the array is of a primitive type (or String) checked earlier.
                items.put("type", mapType(clazz.getComponentType()));
                paramJson.put("items", items);
            } else {
                // Otherwise, use the mapped type directly.
                paramJson.put("type", mapType(clazz));
            }
            // Add the parameter description.
            paramJson.put("description", paramDesc[i]);

            // Insert into properties and mark as required.
            properties.put(paramNames[i], paramJson);
            required.put(paramNames[i]);
        }

        // Finalize the parameters object.
        parameters.put("properties", properties);
        parameters.put("required", required);

        // Add "parameters" to the function JSON.
        functionJson.put("parameters", parameters);

        return functionJson;
    }

    public static JSONObject constructPayload(JSONArray tools, JSONArray contents) {
        return new JSONObject().put("contents", contents).put("tools", tools);
    }

    // Helper function to map from Java types to JSON schema types.
    private static String mapType(Class<?> cls) {
        if (cls.equals(String.class)) {
            return "string";
        } else if (cls.equals(int.class) || cls.equals(Integer.class)) {
            return "integer";
        } else if (cls.equals(boolean.class) || cls.equals(Boolean.class)) {
            return "boolean";
        } else if (cls.equals(double.class) || cls.equals(Double.class)
                || cls.equals(float.class) || cls.equals(Float.class)) {
            return "number";
        }
        // For any other type, default to string.
        return "string";
    }

    public static JSONObject encodeMessage(String role,JSONObject parts ){
        return new JSONObject().put("role", role).put("parts", parts);
    }

    public static JSONObject constructUSerContent(String text, FunctionResponse<?>[] responses){
        JSONObject data = new JSONObject();
        data.put("role", "user");
        JSONArray parts = new JSONArray();
        for(FunctionResponse<?> res : responses){
            parts.put(new JSONObject().put("functionResponse",res.toJSON()));
        }
        parts.put(new JSONObject().put("text", text));
        data.put("parts", parts);
        return data;
    }
    public static JSONObject constructModelContent(JSONObject retContentPart){
        return new JSONObject().put("role", "model").put("parts", new JSONArray().put(retContentPart));
    }
}