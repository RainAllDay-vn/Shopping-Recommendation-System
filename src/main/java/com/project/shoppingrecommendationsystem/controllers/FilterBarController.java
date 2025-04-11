package com.project.shoppingrecommendationsystem.controllers;

<<<<<<< HEAD
import java.util.LinkedList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class FilterBarController {
    static FilterBarController instance;
    private List<String> vendorFilter = new LinkedList<>();
    private List<String> priceFilter = new LinkedList<>();
    private List<String> screenSizeFilter = new LinkedList<>();
    private List<String> purposes = new LinkedList<>();


    @FXML
    private ScrollPane filterBar;

    @FXML
    private Label greenText;

    @FXML
    private VBox priceOptions;

    @FXML
    private VBox purposeOptions;

    @FXML
    private VBox screenSizeOptions;

    @FXML
    private VBox vendorOptions;


    @FXML
    public void initialize(){
        if(instance == null) {
            instance = this;
        }
        else{
            System.err.println("More than one filter controller");
        }
        setCheckBoxColumn(-9, vendorOptions, vendorFilter);
        setCheckBoxColumn(0, priceOptions, priceFilter);
        setCheckBoxColumn(0, purposeOptions, purposes);
        setCheckBoxColumn(0, screenSizeOptions, screenSizeFilter);
        
    }
    void setCheckBoxColumn(int checkAllIndex, VBox node, List<String> listToAddTo){
        final List<Node> checkBoxes = node.getChildren();
        final CheckBox checkAll = checkAllIndex>= 0 && checkAllIndex < checkBoxes.size()?
                                    (CheckBox)checkBoxes.get(checkAllIndex): null;
        EventHandler onMouseClicked = new EventHandler<ActionEvent>() {
            static int checkedBoxCount =0;
            @Override
            public void handle(ActionEvent event) {
                CheckBox source = (CheckBox)event.getSource();
                //source.setSelected(!source.isSelected());
                if(source == checkAll){
                    if(!source.isSelected()){
                        checkedBoxCount = 0;
                        listToAddTo.clear();
                    }
                    else{
                        checkedBoxCount = checkBoxes.size()-1;
                    }
                    for(Node child : checkBoxes){
                        if(child == source){
                            continue;
                        }
                        CheckBox box = (CheckBox)child;
                        if(source.isSelected() && !box.isSelected()){
                            listToAddTo.add(box.getText());
                        }
                        box.setSelected(source.isSelected());
                    }
                }
                else{
                    if(source.isSelected()){
                        checkedBoxCount++;
                        listToAddTo.add(source.getText());
                        if(checkedBoxCount == checkBoxes.size()-1){
                            checkAll.setSelected(true);
                        }
                    }
                    else{
                        checkedBoxCount--;
                        listToAddTo.remove(source.getText());
                        checkAll.setSelected(false);
                    }
                }
            }
            
        };
        for(Node box:checkBoxes){
            ((CheckBox)box).setOnAction(onMouseClicked);
        }
    }

    static public IFilterData getFilterData(){
        return new IFilterData() {

            @Override
            public List<String> getVendor() {
                return instance.vendorFilter;
            }

            @Override
            public List<String> getScreenSize() {
                return instance.screenSizeFilter;
            }

            @Override
            public List<String> getPrice() {
                return instance.priceFilter;
            }


            @Override
            public List<String> getPurpose() {
                return instance.purposes;
            }
            @Override
            public String toString() {
                return 
                        "vendors: " + instance.vendorFilter.toString() + 
                        "\nprice: " + instance.priceFilter.toString() + 
                        "\npurpose: " +  instance.purposes.toString() + 
                        "\nscreenSize: " + instance.screenSizeFilter.toString();
            }
        };
=======
import com.project.shoppingrecommendationsystem.Messenger;
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
>>>>>>> hieu/4-design-homepage
    }
}
