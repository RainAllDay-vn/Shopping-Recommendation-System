package com.project.shoppingrecommendationsystem.views;

import java.io.IOException;
import java.util.Stack;

import com.project.shoppingrecommendationsystem.controllers.IPage;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class ProgramLayout {

    static ProgramLayout instance;
    private Stack<IPage> stack = new Stack<>();
    private IPage currentPage;
    
    @FXML
    private BorderPane rootPane;

    @FXML
    public void initialize(){
        setUpInstance();
        try{
            ProgramPages.init();  
            loadComponent();  
        }catch (IOException e){
            System.err.println(e);
        }
        setCurrentPage(ProgramPages.productGrid);
    }

    

    void setUpInstance(){
        //typical singleton instance init
        if(instance == null){
            instance = this;
        }
        else{
            System.out.println("More than one program instance");
        }
    }
    
    void loadComponent() throws IOException{
        rootPane.getStylesheets().add(getClass().getResource
            ("/com/project/shoppingrecommendationsystem/styling.css").toExternalForm());
        rootPane.setTop(ProgramPages.header);
        rootPane.setLeft(ProgramPages.filterPanel);
        rootPane.setBottom(ProgramPages.footer);
        rootPane.setRight(ProgramPages.chatBox);
    }


    static void setCurrentPage(IPage page){
        instance.currentPage = page;
        instance.rootPane.setCenter(page.getRootAsParent());
    }

    public static void push(IPage page){
        instance.stack.push(instance.currentPage);
        setCurrentPage(page);
    }
    public static void pop(){
        setCurrentPage(instance.stack.pop());
    }
}
