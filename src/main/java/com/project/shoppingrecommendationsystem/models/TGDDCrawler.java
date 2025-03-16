package com.project.shoppingrecommendationsystem.models;


import com.opencsv.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TGDDCrawler extends Crawler {
    private List<Product> results = new ArrayList<>();
    WebDriver driver;
    @Override
    public void crawl() {
        driver = new ChromeDriver();
        results = new ArrayList<>();
        String url = "https://www.thegioididong.com/laptop#c=44&o=13&pi=30";
        List<String> sources= new LinkedList<>();
        List<String> names = new LinkedList<>();
        List<Integer> prices = new LinkedList<>();
        try {
            driver.get(url);
            List<WebElement> elements = driver.findElements(By.xpath("/html/body/div[7]/section[5]/div[1]/ul/*/a"));
            for(WebElement element : elements){
                String source = "https://www.thegioididong.com" + element.getDomAttribute("href");
                String name = element.getDomAttribute("data-name");
                int price = (int)Double.parseDouble(element.getDomAttribute("data-price"));
                //results.add(new Laptop(0,name,"", price,source));
                sources.add(source);
                names.add(name);
                prices.add(price);
            }
            for(int i = 0 ; i< sources.size(); i++){
                crawlDetailed(sources.get(i));

            }
            save();
        } catch (Exception e) {
            System.out.println("An error has occurred when crawling data:");
            System.out.println(e.getMessage());
        } finally {
            driver.quit();
        }
    }

    String crawlDetailed(String source){
        String data = "";
        ///html/body/section/div[2]/div[1]/div[6]/div[2]/div[1]/ul/li[1]/aside[2]/span
        ///html/body/section/div[2]/div[1]/div[6]/div[2]/div[1]/ul/li[2]/aside[2]/span
        ///html/body/section/div[2]/div[1]/div[6]/div[2]/div[1]/ul/li[3]/aside[2]/span
        ///html/body/section/div[2]/div[1]/div[6]/div[2]/div[2]/ul/li[1]/aside[2]/a
        ///html/body/section/div[2]/div[1]/div[6]/div[2]/div[2]/ul/li[3]/aside[2]/span
        String path=  "/html/body/section/div[2]/div[1]/div[6]/div[2]/*/ul/*/aside[2]/span";
        driver.get(source);
        List<WebElement> elements = driver.findElements(By.xpath(path));
        for(WebElement element : elements){
            System.out.println(element.getAttribute("textContent"));
        }
        return data;
    }

    public void save(){
        String savePath = "data/FPTShop.csv";
        try (ICSVWriter out = new CSVWriterBuilder(new FileWriter(savePath))
                .withEscapeChar('\\')
                .build()) {
            out.writeNext(new String[]{"id", "name", "description", "price", "sourceURL"});
            for (Product product : results) {
                out.writeNext(new String[]{
                        Integer.toString(product.getId()),
                        product.getName(),
                        product.getDescription(),
                        Integer.toString(product.getPrice()),
                        product.getSourceURL()
                });
            }
        } catch (Exception e){
            System.out.println("An error has occurred when saving data:");
            System.out.println(e.getMessage());
        }
    }

    private void load() {
        /*
        results = new ArrayList<>();
        String loadPath = "data/FPTShop.csv";
        try (CSVReader in = new CSVReader(new FileReader(loadPath))) {
            in.skip(1);
            for (String[] row : in) {
                results.add(new Laptop(
                        Integer.parseInt(row[0]),
                        row[1],
                        row[2],
                        Integer.parseInt(row[3]),
                        row[4]
                ));
            }
        } catch (Exception e){
            System.out.println("An error has occurred when loading data:");
            System.out.println(e.getMessage());
        }
             */
    }
}
