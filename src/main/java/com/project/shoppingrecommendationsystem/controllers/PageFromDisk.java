package com.project.shoppingrecommendationsystem.controllers;

import java.net.URI;
import java.net.URL;

import com.project.shoppingrecommendationsystem.controllers.IPage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;


public class PageFromDisk implements IPage {

    private FXMLLoader loader;
    private Parent view;
    private Object controller;
    public PageFromDisk(URL path){
        loader =new FXMLLoader(path);
        try{
        view = loader.load();
        }catch(Exception e){
            System.err.println(e);
        }
        controller = loader.getController();
    }
    @Override
    public Parent getRootAsParent() {
        return view;
    }

    @Override
    public Object getController() {
        return controller;
    }
    
}
