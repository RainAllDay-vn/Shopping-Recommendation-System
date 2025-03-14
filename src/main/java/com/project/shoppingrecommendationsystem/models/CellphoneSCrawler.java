package com.project.shoppingrecommendationsystem.models;

import com.opencsv.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


public class CellphoneSCrawler extends Crawler{
    private List<Product> results = new ArrayList<>();
    @Override
    public void crawl() {
        WebDriver driver = new ChromeDriver();
        results = new ArrayList<>();
        String url = "https://cellphones.com.vn/laptop.html";
        String buttonXPath = "/html/body/div[1]/div/div/div[3]/div[2]/div/div[8]/div[5]/div/div[2]";
        try {
            driver.get(url);

            //Show all product
            WebElement button = driver.findElement(By.xpath(buttonXPath));
            Actions clickAction = new Actions(driver);
            while (button.getText().contains("Xem")) {
                //((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
                Thread.sleep(500);
                clickAction.moveToElement(button).click().perform();
                System.out.println(button.getText());
                Thread.sleep(500);
            }
            String productCellPath = "html/body/div[1]/div/div/div[3]/div[2]/div/div[8]/div[5]/div/div[1]/*/div[1]/a";
            List<WebElement> elements = driver.findElements(By.xpath(productCellPath));
            for(WebElement element : elements){
                String source = element.getDomAttribute("href");
                String name = element.findElement(By.className("product__name")).getText();
                String price =  element.findElement(By.className("product__price--show")).getText().toString();
                price = price.substring(0, price.length()-1);
                price = price.replaceAll("\\.", "");
                results.add(new Laptop(0, name, "",(int)Double.parseDouble(price) , source));
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
