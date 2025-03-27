package com.project.shoppingrecommendationsystem.controllers;

import com.project.shoppingrecommendationsystem.Messenger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class FilterBarController implements Initializable {
    @FXML
    private VBox vendorOptions;
    private final List<String[]> query = Messenger.getInstance().getQuery();

    public FilterBarController() {
        query.clear();
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
}
