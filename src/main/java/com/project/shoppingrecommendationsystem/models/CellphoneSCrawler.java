package com.project.shoppingrecommendationsystem.models;

import com.opencsv.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class CellphoneSCrawler extends Crawler{
    private List<Product> results = new ArrayList<>();
    WebDriver driver;
    @Override
    public void crawl() {
        driver =  new ChromeDriver();
        results = new ArrayList<>();
        String url = "https://cellphones.com.vn/laptop.html";
        String buttonXPath = "/html/body/div[1]/div/div/div[3]/div[2]/div/div[8]/div[5]/div/div[2]";
        try {
            driver.get(url);

            
            /*
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
 */
            List<String> sources = new LinkedList<>();
            List<String> names = new LinkedList<>();
            List<String> imagesURL = new LinkedList<>();
            List<Integer> prices = new LinkedList<>();

            String productCellPath = "html/body/div[1]/div/div/div[3]/div[2]/div/div[8]/div[5]/div/div[1]/*/div[1]/a";
            List<WebElement> elements = driver.findElements(By.xpath(productCellPath));
            for(WebElement element : elements){
                i--;
                if(i <= 0)break;
                String source = element.getDomAttribute("href");
                WebElement image = element.findElement(By.tagName("img"));
                String imageURL = image.getDomAttribute("src");
                String name = element.findElement(By.className("product__name")).getText();
                String price =  element.findElement(By.className("product__price--show")).getText().toString();
                price = price.substring(0, price.length()-1);
                price = price.replaceAll("\\.", "");
                sources.add(source);
                names.add(name);
                prices.add((int)Double.parseDouble(price));
                imagesURL.add(imageURL);

                //results.add(new Laptop(0, name, "",(int)Double.parseDouble(price) , source,imageURL,attributes));
            }
            for(int i = 0; i< sources.size(); i++){
                Map<String,String> attributes = getDetailedProductInfo(sources.get(i));
                results.add(new Laptop(i,names.get(i), "", prices.get(i), sources.get(i), imagesURL.get(i), attributes));
            }
            save();
        } catch (Exception e) {
            System.out.println("An error has occurred when crawling data:");
            System.out.println(e.getMessage());
        }
    }
    int i = 2;
    Map<String,String> getDetailedProductInfo(String source){
        Map<String,String> data = new HashMap<>();
        boolean done = false;
        new Actions(driver).scrollByAmount(0, 1650).perform();
        do{
            try{
                driver.get(source);
                WebElement temp = driver.findElement(By.id("cpsContent"));
                new Actions(driver).scrollToElement(temp).perform();
                //WebElement show_technical = driver.findElement(By.className("button button__show-modal-technical"));
                //new Actions(driver).scrollToElement(show_technical).scrollByAmount(0, 50).perform();
                Thread.sleep(500);
                //new Actions(driver).moveToElement(show_technical).click().perform();
                

                String path = "//*[@id=\"productDetailV2\"]/section/div[4]/div[2]/div/div/div[2]/div[2]/section/div/ul/*/div/*/div";
                            //*[@id=\"productDetailV2\"]/section/div[4]/div[2]/div/div/div[2]/div[2]/section/div/ul/li[1]/div/div[2]/div
                            //*[@id=\"productDetailV2\"]/section/div[4]/div[2]/div/div/div[2]/div[2]/section/div/ul/li[2]/div/div[1]/div
                //html/body/div[1]/div/div/div[3]/div[2]/div[1]/section/div[4]/div[2]/div[1]/div[1]/div[2]/div[2]/section/div/ul/*/div/div[1]/div

                List<WebElement> elements  = driver.findElements(By.xpath(path));
                if(elements.size() == 0)
                    continue;
                for(WebElement element : elements){
                    String rdata = element.getAttribute("textContent");
                    System.out.println(rdata);
                }
                done = true;

            } catch(Exception exception){
                System.out.println("Error,try again");
            }
        } while(!done);
        return data;
    }

    public void save(){
        String savePath = "C:\\Users\\minhh\\Downloads\\ProjectStuffs\\Shopping-Recommendation-System\\src\\main\\java\\com\\project\\shoppingrecommendationsystem\\models\\data";
        try (ICSVWriter out = new CSVWriterBuilder(new FileWriter(savePath + "\\CellphoneS.csv"))
                .withEscapeChar('\\')
                .build()) {
            out.writeNext(new String[]{"id", "name", "description", "price", "sourceURL","attributes"});
            for (Product product : results) {
                out.writeNext(new String[]{
                        Integer.toString(product.getId()),
                        product.getName(),
                        product.getDescription(),
                        Integer.toString(product.getPrice()),
                        product.getSourceURL(),
                        product.getAttributeAsJSon()
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
