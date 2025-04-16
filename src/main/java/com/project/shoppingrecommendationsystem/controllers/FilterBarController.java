package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.Messenger;
import com.project.shoppingrecommendationsystem.models.chatbots.ChatBotControllable;
import com.project.shoppingrecommendationsystem.models.chatbots.ChatBotFunction;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterBarController extends ChatBotControllable implements Initializable {
    @FXML
    private VBox vendorOptions;
    @FXML
    private VBox priceOptions;
    private final List<String[]> query = Messenger.getInstance().getQuery();

    public FilterBarController() {
        query.clear();
    }

    void setCheckBox(int index, boolean val){
        ((CheckBox)vendorOptions.getChildren().get(index)).setSelected(val);
    }

    @ChatBotFunction(desc = "set brand on brand panel by inputing boolean", paramDesc = {"asus", "apple", "lenovo", "msi", "hp", "acer", "dell", "samsung"})
    void setBrandPanel(boolean asus, boolean apple, boolean lenovo, boolean msi, boolean hp, boolean acer, boolean dell, boolean samsung){
        setCheckBox(0, asus);
        setCheckBox(1, apple);
        setCheckBox(2, lenovo);
        setCheckBox(3, msi);
        setCheckBox(4, hp);
        setCheckBox(5, dell);
        setCheckBox(6, samsung);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
            if (field[0].equals("brand")){
                String[] newField = Arrays.copyOf(field, field.length+1);
                newField[newField.length-1] = brandName;
                query.set(i, newField);
                return;
            }
        }
        query.add(new String[] {"brand", "in", brandName});
    }

    private void removeBrandFromQuery(String brandName) {
        for (int i = 0; i < query.size(); i++) {
            String[] field = query.get(i);
            if (field[0].equals("brand")){
                String[] newField = Arrays.stream(field).filter(brand -> !brand.equals(brandName))
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
