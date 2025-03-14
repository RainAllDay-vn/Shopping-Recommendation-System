package com.project.shoppingrecommendationsystem.models;

import com.opencsv.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FPTShopCrawler extends Crawler {
    private List<Product> results = new ArrayList<>();
    public FPTShopCrawler() {
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver-win64\\chromedriver.exe");
        try {
            load();
        } catch (Exception ignored) {
            System.out.println("Failed to load from file, start crawling again ...");
            crawl();
        }
    }

    @Override
    public void crawl() {
        WebDriver driver = new ChromeDriver();
        results = new ArrayList<>();
        String url = "https://fptshop.com.vn/may-tinh-xach-tay";
        try {
            driver.get(url);
            WebElement container = driver.findElement(By.className("grow"));
            WebElement button = container.findElement(By.xpath("./div/button"));
            int time = Integer.parseInt(button.getText().replaceAll("\\D", ""))/16 + 1;
            for (int i = 1; i <= time; i++) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
                Thread.sleep(500);
                button.click();
                Thread.sleep(500);
            }
            List<WebElement> elements = container.findElements(By.xpath("./*[2]/div"));
            for (int id=0; id<elements.size(); id++) {
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", elements.get(id));
                    WebElement infoCard = elements.get(id).findElement(By.xpath("./div/div[2]"));
                    String name = infoCard.findElement(By.xpath("./h3")).getText();
                    String description = "";
                    String priceStr = infoCard.findElement(By.xpath("./div/p[2]"))
                            .getText()
                            .replaceAll("\\D", "");
                    int price = Integer.parseInt(priceStr);
                    String sourceURL = infoCard.findElement(By.xpath("./h3/a")).getDomAttribute("href");
                    Laptop laptop = new Laptop(id, name, description, price, sourceURL);
                    results.add(laptop);
                } catch (Exception e) {
                    System.out.println("Error, skipping item ...");
                }
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
            throw new RuntimeException("An error has occurred when loading data.");
        }
    }
}

