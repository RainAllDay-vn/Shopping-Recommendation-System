package com.project.shoppingrecommendationsystem.models;


import com.opencsv.*;

import org.jsoup.Jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.jsoup.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TGDDCrawler extends Crawler {
    private List<Product> results = new ArrayList<>();
    @Override
    public void crawl() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        WebDriver driver = new ChromeDriver(options);
        results = new ArrayList<>();
        String url = "https://www.thegioididong.com/laptop#c=44&o=13&pi=18";
        List<String> sources= new LinkedList<>();
        List<String> names = new LinkedList<>();
        List<Integer> prices = new LinkedList<>();
        
        try {
            driver.get(url);
            String pageSource = driver.getPageSource();
            driver.quit();
            Document rawHtml = Jsoup.parse(pageSource);
            Elements elements =  rawHtml.selectXpath("/html/body/div[7]/section[5]/div[1]/ul/*/a");
            //List<WebElement> elements = driver.findElements(By.xpath("/html/body/div[7]/section[5]/div[1]/ul/*/a"));
            for(Element element : elements){
                String source = "https://www.thegioididong.com" + element.attr("href");
                String name = element.attr("data-name");
                int price = (int)Double.parseDouble(element.attr("data-price"));
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
            //driver.quit();
        }
    }

    String crawlDetailed(String source){
        String data = "";
        Document page;
        try{
            page= Jsoup.connect(source).get();
        } catch(IOException e){
            System.err.println("Failed to get page");
            return data;
        }
        ///html/body/section/div[2]/div[1]/div[6]/div[2]/div[1]/ul/li[1]/aside[2]/span
        ///html/body/section/div[2]/div[1]/div[6]/div[2]/div[1]/ul/li[2]/aside[2]/span
        ///html/body/section/div[2]/div[1]/div[6]/div[2]/div[1]/ul/li[3]/aside[2]/span
        ///html/body/section/div[2]/div[1]/div[6]/div[2]/div[2]/ul/li[1]/aside[2]/a
        ///html/body/section/div[2]/div[1]/div[6]/div[2]/div[2]/ul/li[3]/aside[2]/span
        String path=  "/html/body/section/div[2]/div[1]/div[6]/div[2]/*/ul/*/aside[2]/span";
        List<Element> elements = page.selectXpath(path);
        for(Element element : elements){
            System.out.println(element.text());
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
