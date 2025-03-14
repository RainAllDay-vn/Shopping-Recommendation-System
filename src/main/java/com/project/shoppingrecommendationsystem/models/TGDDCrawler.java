package com.project.shoppingrecommendationsystem.models;


import com.opencsv.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class TGDDCrawler extends Crawler {
    private List<Product> results = new ArrayList<>();
    @Override
    public void crawl() {
        WebDriver driver = new ChromeDriver();
        results = new ArrayList<>();
        String url = "https://www.thegioididong.com/laptop#c=44&o=13&pi=30";
        try {
            driver.get(url);
            List<WebElement> elements = driver.findElements(By.xpath("/html/body/div[7]/section[5]/div[1]/ul/*/a"));
            for(WebElement element : elements){
                String source = "https://www.thegioididong.com/laptop" + element.getDomAttribute("href");
                String name = element.getDomAttribute("data-name");
                int price = (int)Double.parseDouble(element.getDomAttribute("data-price"));
                results.add(new Laptop(0,name,"", price,source));
            }
            save();
        } catch (Exception e) {
            System.out.println("An error has occurred when crawling data:");
            System.out.println(e.getMessage());
        } finally {
            driver.quit();
        }
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
    }
}
