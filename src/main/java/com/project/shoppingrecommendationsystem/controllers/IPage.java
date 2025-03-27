package com.project.shoppingrecommendationsystem.controllers;

import javafx.scene.Parent;

public interface IPage {
    public Parent getRootAsParent();
    public Object getController();
}
