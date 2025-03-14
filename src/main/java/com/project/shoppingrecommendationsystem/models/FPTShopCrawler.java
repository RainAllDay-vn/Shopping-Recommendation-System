package com.project.shoppingrecommendationsystem.models;

import com.opencsv.*;
import com.project.shoppingrecommendationsystem.CrawlerTest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.JsonToWebElementConverter;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FPTShopCrawler extends Crawler {
    private List<Product> results = new ArrayList<>();
    String resourceURL = Objects.requireNonNull(CrawlerTest.class.getResource("")).getPath().replace("%20", " ") + "data/FPTShop";
    public FPTShopCrawler() {
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver-win64\\chromedriver.exe");
        try {
            load();
        } catch (Exception e) {
            System.out.println("Cannot load crawled data");
        }
    }

    @Override
    public void crawl() {
        crawlInfoCard();
        crawlProductsInfo();
        save();
    }

    private void crawlInfoCard() {
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
        try (ICSVWriter out = new CSVWriterBuilder(new FileWriter(resourceURL + "/FPTShop.csv"))
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
        try (ICSVWriter out = new CSVWriterBuilder(new FileWriter(resourceURL + "/ProductImages.csv"))
                .withEscapeChar('\\')
                .build()) {
            out.writeNext(new String[]{"productId", "imageURL"});
            for (Product product : results) {
                for(Image image: product.getImages()){
                    out.writeNext(new String[]{
                            Integer.toString(product.getId()),
                            image.getURL()
                    });
                }
            }
        } catch (Exception e){
            System.out.println("An error has occurred when saving images:");
            System.out.println(e.getMessage());
        }
        try (ICSVWriter out = new CSVWriterBuilder(new FileWriter(resourceURL + "/Hardware.csv"))
                .withEscapeChar('\\')
                .build()) {
            out.writeNext(new String[]{"productId", "key", "value"});
            for (Product product : results) {
                for(Map.Entry<String, String> entry: product.getHardwareMap().entrySet()){
                    out.writeNext(new String[]{
                            Integer.toString(product.getId()),
                            entry.getKey(),
                            entry.getValue()
                    });
                }
            }
        } catch (Exception e){
            System.out.println("An error has occurred when saving hardware:");
            System.out.println(e.getMessage());
        }
    }

    private void load() {
        results = new ArrayList<>();
        try (CSVReader in = new CSVReader(new FileReader(resourceURL + "/FPTShop.csv"))) {
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
        try (CSVReader in = new CSVReader(new FileReader(resourceURL + "/ProductImages.csv"))) {
            in.skip(1);
            for (String[] row : in) {
                results.get(Integer.parseInt(row[0])).addImage(row[1]);
            }
        } catch (Exception e){
            throw new RuntimeException("An error has occurred when loading images.");
        }
        try (CSVReader in = new CSVReader(new FileReader(resourceURL + "/Hardware.csv"))) {
            in.skip(1);
            for (String[] row : in) {
                results.get(Integer.parseInt(row[0])).addHardware(row[1], row[2]);
            }
        } catch (Exception e){
            throw new RuntimeException("An error has occurred when loading images.");
        }
    }

    private void crawlProductsInfo(){
        WebDriver driver = new ChromeDriver();
        for (Product product : results) {
            System.out.printf("Crawling product: %s (Id: %d)%n", product.getName(), product.getId());
            driver.get("https://fptshop.com.vn" + product.getSourceURL());
            crawlDescription(product, driver);
            crawlHardware(product, driver);
            crawlImage(product, driver);
        }
        driver.quit();
    }

    private void crawlDescription(Product product, WebDriver driver) {
        try {
            Document doc = Jsoup.parse(Objects.requireNonNull(driver.getPageSource()));
            Element descriptionContainer = doc.getElementsByClass("description-container").first();
            if (descriptionContainer == null) {
                System.out.println("Description container not found. Set description to NONE.");
                product.setDescription("NONE");
                return;
            }
            for (Element description : descriptionContainer.children()) {
                if (description.text().isBlank()) continue;
                product.setDescription(description.text());
            }
        } catch (Exception e) {
            System.out.println("An error has occurred when crawling description for product ...");
        }
    }

    private void crawlHardware(Product product, WebDriver driver) {
        try {
            WebElement expandButton = driver.findElement(By.cssSelector(".flex.items-center.text-blue-blue-7.b2-medium"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", expandButton);
            Thread.sleep(500);
            expandButton.click();
            Thread.sleep(500);
            Document doc = Jsoup.parse(Objects.requireNonNull(driver.getPageSource()));
            for (Element element: doc.getElementsByClass("flex gap-2 border-b border-dashed border-b-iconDividerOnWhite py-1.5")) {
                Elements elements = element.children();
                product.addHardware(elements.get(0).text(), elements.get(1).text());
            }
        } catch (Exception e) {
            System.out.println("An error has occurred when crawling hardware's' information for product.");
        }
    }

    private void crawlImage(Product product, WebDriver driver) {
        try {
            Document doc = Jsoup.parse(Objects.requireNonNull(driver.getPageSource()));
            Element descriptionContainer = doc.getElementsByClass("description-container").first();
            if (descriptionContainer == null) {
                System.out.println("Description container not found.");
                return;
            }
            Element imageSlider = descriptionContainer.previousElementSibling();
            if (imageSlider != null) {
                for (Element image : imageSlider.select("img")) {
                    String srcset = image.attr("srcset");
                    String imageURL = srcset.split(",")[0].split(" ")[0];
                    product.addImage(imageURL);
                }
            }
            for (Element image : descriptionContainer.select("img")) {
                product.addImage(image.attr("src"));
            }
        } catch (Exception e) {
            System.out.println("An error has occurred when crawling images for product ...");
        }
    }
}

