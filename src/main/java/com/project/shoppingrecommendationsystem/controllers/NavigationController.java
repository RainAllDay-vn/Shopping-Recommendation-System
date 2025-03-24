package com.project.shoppingrecommendationsystem.controllers;

import java.util.Stack;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationController {
    private Stage stage;
    private Scene scene;
    Stack<IPage> stack;
    IPage currentPage;
    static NavigationController instance;
    public static void init(Stage stage,IPage homePage){
        instance = new NavigationController();
        instance.stage = stage;
        instance.scene = new Scene(homePage.getRootAsParent());
        instance.stack = new Stack<>();
        instance.currentPage = homePage;
        instance.scene.setRoot(homePage.getRootAsParent());
        stage.setScene(instance.scene);
    }
    public static void push(IPage page){
        instance.stack.push(instance.currentPage);
        instance.currentPage = page;
        instance.scene.setRoot(page.getRootAsParent());
    } 
    public static void pop(){
        instance.currentPage = instance.stack.pop();
        instance.scene.setRoot(instance.currentPage.getRootAsParent());
    } 
}
