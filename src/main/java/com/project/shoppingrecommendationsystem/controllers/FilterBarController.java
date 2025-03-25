package com.project.shoppingrecommendationsystem.controllers;

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
    }
}
