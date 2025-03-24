package com.project.shoppingrecommendationsystem.controllers;

import java.util.List;

public interface IFilterData {
    public List<String> getVendor();
    public List<String> getScreenSize();
    public List<String> getPrice();
    public List<String> getPurpose();
}
