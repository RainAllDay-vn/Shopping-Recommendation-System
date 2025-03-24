package com.project.shoppingrecommendationsystem.controllers;

import java.util.LinkedList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
        for(int i = 0; i< node.getChildren().size(); i++){
            if(i == checkAllIndex){
                ((CheckBox)node.getChildren().getFirst()).selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
                        for(Node child: node.getChildren()){
                            ((CheckBox)child).setSelected(arg2);
                        }
                    }
                });
            }
            else{
                CheckBox checkBox = (CheckBox)node.getChildren().get(i);
                checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

                    @Override
                    public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
                        if(arg2){
                            listToAddTo.add(checkBox.getText());
                        }
                        else{
                            listToAddTo.remove(checkBox.getText());
                        }
                    }
                    
                });
            }
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
