package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.Messenger;
import com.project.shoppingrecommendationsystem.models.chatbots.ChatBot;
import com.project.shoppingrecommendationsystem.models.chatbots.ChatBotCallable;
import com.project.shoppingrecommendationsystem.models.chatbots.ChatBotCallableInfo;
import com.project.shoppingrecommendationsystem.models.chatbots.ChatBotParameter;
import com.project.shoppingrecommendationsystem.models.chatbots.ChatBotInputParameter;
import com.project.shoppingrecommendationsystem.models.chatbots.ChatBotReturn;
import com.project.shoppingrecommendationsystem.models.chatbots.ChatBotStaticCallableInfo;
import com.project.shoppingrecommendationsystem.models.chatbots.ChatBotStaticParam;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterBarController implements Initializable {
    @FXML
    private VBox vendorOptions;
    @FXML
    private VBox priceOptions;
    private final List<String[]> query = Messenger.getInstance().getQuery();

    public FilterBarController() {
        query.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize vendor checkboxes.
        for (Node node : vendorOptions.getChildren()) {
            if (!(node instanceof CheckBox checkBox)) {
                continue;
            }
            checkBox.setOnAction(event -> {
                if (checkBox.isSelected()) {
                    addBrandToQuery(checkBox.getText());
                } else {
                    removeBrandFromQuery(checkBox.getText());
                }
            });
        }

        initializePriceRadioButtons();

        String desc = "Controls filter panel buttons based on provided command. For example, calling with a command 'select_all' could simulate pressing all checkboxes, "
                + "or 'clear_filters' to reset filters.";
        ChatBotCallable action = new ChatBotCallable() {

            @Override
            public ChatBotReturn call(ChatBotInputParameter... params) {
                // Retrieve the "command" parameter from the input.
                Integer brand = -1;
                for (ChatBotInputParameter param : params) {
                    System.out.println(param.getName());
                    if ("brand".equals(param.getName())) {
                        brand = (Integer)param.getValue();
                        if(brand < vendorOptions.getChildren().size()){
                            CheckBox box =  (CheckBox)vendorOptions.getChildren().get(brand);
                            box.setSelected(true);
                        }
                    }
                }
                System.out.println("Filter command received: " + brand);
                return new ChatBotReturn() {
                    @Override
                    public Class<?> getType() {
                        return String.class;
                    }

                    @Override
                    public Object getValue() {
                        return "Hi from gemini";
                    }
                    @Override
                    public JSONObject toJSON() {
                        return new JSONObject().put("res", "Hi");
                    }
                };
            }

            @Override
            public ChatBotCallableInfo getInfo() {
                ChatBotParameter[] params = {
                    new ChatBotStaticParam(Integer.class, "brand","can choose from Asus, Apple, Lenovo, MSI, HP, Acer, Dell, Samsung. Return the index of the list " )
                };
                String pramDesc = "Controls filter panel buttons based on provided command.";
                return new ChatBotStaticCallableInfo("filter", pramDesc, params);
            }
        };
        
        ChatBot.registerAction("filter", desc, action);
    }

    private void initializePriceRadioButtons() {
        ToggleGroup priceGroup = new ToggleGroup();
        for (Node node : priceOptions.getChildren()) {
            if (!(node instanceof RadioButton radioButton)) {
                continue;
            }
            radioButton.setToggleGroup(priceGroup);
        }
        priceGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            RadioButton radioButton = (RadioButton) newValue;
            if (radioButton.getUserData() == null) {
                removePriceFromQuery();
                return;
            }
            String buttonValue = radioButton.getUserData().toString();
            String[] bound = new String[2];
            if (buttonValue.matches("\\d+-")) {
                bound[0] = "0";
                bound[1] = buttonValue.substring(0, buttonValue.length() - 1) + "000000";
            } else if (buttonValue.matches("\\d+\\+")) {
                bound[0] = buttonValue.substring(0, buttonValue.length() - 1) + "000000";
                bound[1] = String.valueOf(Integer.MAX_VALUE);
            } else {
                Matcher matcher = Pattern.compile("(\\d+)-(\\d+)").matcher(buttonValue);
                if (matcher.find()) {
                    bound[0] = matcher.group(1) + "000000";
                    bound[1] = matcher.group(2) + "000000";
                } else {
                    bound[0] = "0";
                    bound[1] = String.valueOf(Integer.MAX_VALUE);
                }
            }
            addPriceToQuery(bound);
        });
    }

    private void addBrandToQuery(String brandName) {
        for (int i = 0; i < query.size(); i++) {
            String[] field = query.get(i);
            if (field[0].equals("brand")) {
                String[] newField = Arrays.copyOf(field, field.length + 1);
                newField[newField.length - 1] = brandName;
                query.set(i, newField);
                return;
            }
        }
        query.add(new String[]{"brand", "in", brandName});
    }

    private void removeBrandFromQuery(String brandName) {
        for (int i = 0; i < query.size(); i++) {
            String[] field = query.get(i);
            if (field[0].equals("brand")) {
                String[] newField = Arrays.stream(field)
                        .filter(brand -> !brand.equals(brandName))
                        .toArray(String[]::new);
                if (newField.length < 3) {
                    query.remove(i);
                    return;
                }
                query.set(i, newField);
                return;
            }
        }
    }

    private void addPriceToQuery(String[] bound) {
        removePriceFromQuery();
        System.out.println(Arrays.toString(bound));
        query.add(new String[]{"price", "between", bound[0], bound[1]});
    }

    private void removePriceFromQuery() {
        query.removeIf(field -> field[0].equals("price"));
    }
}
