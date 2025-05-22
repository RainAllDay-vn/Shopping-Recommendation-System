package com.project.shoppingrecommendationsystem.models.crawlers.smartphones;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.models.Product;
import com.project.shoppingrecommendationsystem.models.SmartPhone;
import com.project.shoppingrecommendationsystem.models.crawlers.Crawler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class SmartPhoneCrawler implements Crawler {
    final ObjectMapper mapper;
    final String resourceURL;
    private final CSVParser parser;
    String[] phoneColumn;
    String[] descriptionColumn;
    String[] propertiesColumn;
    String[] reviewColumn;

    SmartPhoneCrawler(String folder) {
        this.mapper = new ObjectMapper();
        this.resourceURL = Objects.requireNonNull(ShoppingApplication.class.getResource(""))
                .getPath()
                .replace("%20", " ") + "data/smartphones/" + folder;
        File resourceDir = new File(this.resourceURL + "images/");
        if (!resourceDir.exists()) {
            if (!resourceDir.mkdirs()) {
                throw new RuntimeException("Unable to create directory " + this.resourceURL);
            }
        }
        parser = new CSVParserBuilder()
                .withSeparator(',')
                .withEscapeChar('\\')
                .withQuoteChar('\'')
                .build();
        this.descriptionColumn = new String[]{"local_id", "type", "description"};
        reviewColumn = new String[]{"local_id", "created", "content", "score", "username"};
    }

    @Override
    public void crawl() {
        crawl(Integer.MAX_VALUE);
    }

    @Override
    public abstract void crawl(int limit);

    @Override
    public List<Product> getAll() {
        return getSmartPhones().stream()
                .map(phone -> (Product) phone)
                .collect(Collectors.toList());
    }

    abstract SmartPhone parseSmartPhone(String[] laptopRow, List<String[]> descriptionRow, String[] propertiesRow, List<String[]> reviews);

    /**
     * Retrieves a list of SmartPhone objects from the saved data files.
     * <p>
     * This method reads the previously saved smartphone data from the respective files (e.g., CSV files containing
     * description and properties) and constructs a list of smartphone objects.
     *
     * @return A List of SmartPhone objects representing the crawled smartphone data.
     */
    public List<SmartPhone> getSmartPhones() {
        List<SmartPhone> phones = new LinkedList<>();
        try (CSVReader phoneReader = getCSVReader("phone.csv");
             CSVReader descriptionReader = getCSVReader("description.csv");
             CSVReader propertiesReader = getCSVReader("properties.csv");
             CSVReader reviewsReader = getCSVReader("reviews.csv")) {
            String[] phoneRow;
            while ((phoneRow = phoneReader.readNext()) != null) {
                List<String[]> descriptions = new LinkedList<>();
                while (true) {
                    String[] descriptionRow = descriptionReader.peek();
                    if (descriptionRow == null) break;
                    if (!descriptionRow[0].equals(phoneRow[0])) break;
                    descriptions.add(new String[]{descriptionRow[1], descriptionRow[2]});
                    descriptionReader.readNext();
                }
                String[] propertiesRow = propertiesReader.readNext();
                List<String[]> reviews = new LinkedList<>();
                while (true) {
                    String[] reviewRow = reviewsReader.peek();
                    if (reviewRow == null) break;
                    if (!reviewRow[0].equals(phoneRow[0])) break;
                    reviews.add(new String[]{reviewRow[1], reviewRow[2], reviewRow[3], reviewRow[4]});
                    reviewsReader.readNext();
                }
                try {
                    phones.add(parseSmartPhone(phoneRow, descriptions, propertiesRow, reviews));
                } catch (Exception e){
                    System.err.println("[ERROR] : An error occurred while trying to parse smartphone #" + phoneRow[0]);
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] : There was an error when accessing saving file");
            System.out.println(e.getMessage());
        }
        return phones;
    }

    /**
     * Creates an ICSVWriter for a given filename with the append option set to false.
     *
     * @param filename The name of the CSV file.
     * @return An ICSVWriter instance.
     * @throws IOException If an I/O error occurs.
     */
    private ICSVWriter getCSVWriter (String filename) throws IOException {
        return getCSVWriter(filename, false);
    }

    /**
     * Creates an ICSVWriter for a given filename with an option to append.
     *
     * @param filename The name of the CSV file.
     * @param append   True to append to the file, false to overwrite.
     * @return An ICSVWriter instance.
     * @throws IOException If an I/O error occurs.
     */
    private ICSVWriter getCSVWriter (String filename, boolean append) throws IOException {
        return new CSVWriterBuilder(new FileWriter(resourceURL + filename, append))
                .withParser(parser)
                .build();
    }

    /**
     * Creates a CSVReader for a given filename.
     *
     * @param filename The name of the CSV file to read.
     * @return A CSVReader instance configured with the predefined CSVParser.
     * @throws IOException If an I/O error occurs while creating the reader.
     */
    private CSVReader getCSVReader (String filename) throws IOException, CsvValidationException {
        File file = new File(resourceURL + filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        CSVReader reader = new CSVReaderBuilder(new FileReader(resourceURL + filename))
                .withCSVParser(parser)
                .build();
        reader.readNext();
        return reader;
    }


    void resetSave () {
        try (ICSVWriter laptopWriter = getCSVWriter("phone.csv");
             ICSVWriter descriptionWriter = getCSVWriter("description.csv");
             ICSVWriter propertiesWriter = getCSVWriter("properties.csv");
             ICSVWriter reviewsWriter = getCSVWriter("reviews.csv")) {
            laptopWriter.writeNext(phoneColumn);
            descriptionWriter.writeNext(descriptionColumn);
            propertiesWriter.writeNext(propertiesColumn);
            reviewsWriter.writeNext(reviewColumn);
        } catch (Exception e){
            System.err.println("[ERROR] : There was an error when accessing saving file");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Saves a single smartphone row to the laptop CSV file.
     *
     * @param phoneRow A String array containing smartphone information.
     */
    void savePhoneRow(String[] phoneRow) {
        try (ICSVWriter csvWriter = getCSVWriter("phone.csv", true)) {
            csvWriter.writeNext(phoneRow);
        } catch (Exception e) {
            System.err.println("[ERROR] : There was an error when saving to phone file");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Saves a single description row to the description CSV file.
     *
     * @param descriptions A String array containing product description.
     */
    void saveDescriptions(List<String[]> descriptions) {
        try (ICSVWriter csvWriter = getCSVWriter("description.csv", true)) {
            for (String[] descriptionRow : descriptions) {
                csvWriter.writeNext(descriptionRow);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] : There was an error when saving to description file");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Saves a single properties row to the properties CSV file.
     *
     * @param propertiesRow A String array containing product properties.
     */
    void savePropertiesRow(String[] propertiesRow) {
        try (ICSVWriter csvWriter = getCSVWriter("properties.csv", true)) {
            csvWriter.writeNext(propertiesRow);
        } catch (Exception e) {
            System.err.println("[ERROR] : There was an error when saving to properties file");
            System.out.println(e.getMessage());
        }
    }

    void saveReviews(List<String[]> reviews) {
        try (ICSVWriter csvWriter = getCSVWriter("reviews.csv", true)) {
            for (String[] reviewRow : reviews) {
                csvWriter.writeNext(reviewRow);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] : There was an error when saving to reviews file");
            System.out.println(e.getMessage());
        }
    }
}
