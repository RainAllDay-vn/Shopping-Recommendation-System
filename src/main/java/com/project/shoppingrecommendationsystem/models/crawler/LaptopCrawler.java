package com.project.shoppingrecommendationsystem.models.crawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import com.project.shoppingrecommendationsystem.ShoppingApplication;
import com.project.shoppingrecommendationsystem.models.Laptop;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public abstract class LaptopCrawler {
    final ObjectMapper mapper;
    final String resourceURL;
    private final CSVParser parser;
    String[] laptopColumn;
    String[] descriptionColumn;
    String[] propertiesColumn;
    String[] reviewColumn;

    LaptopCrawler(String folder) {
        this.mapper = new ObjectMapper();
        this.resourceURL = Objects.requireNonNull(ShoppingApplication.class.getResource(""))
                .getPath()
                .replace("%20", " ") + folder;
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
        reviewColumn = new String[]{"local_id", "created", "content", "score", "username"};
    }

    /**
     * Crawls laptop information, removing existing save files (if exists) and fetching fresh data.
     */
    public abstract void crawlLaptops();

    /**
     * Crawls a limited number of laptop entries, removing existing save files (if exists) and fetching fresh data.
     *
     * @param limit The maximum number of laptops to crawl.
     */
    public abstract void crawlLaptops(int limit);

    abstract Laptop parseLaptop(String[] laptopRow, String[] descriptionRow, String[] propertiesRow, List<String[]> reviews);

    /**
     * Retrieves a list of Laptop objects from the saved data files.
     * <p>
     * This method reads the previously saved laptop data from the respective files (e.g., CSV files containing
     * description and properties) and constructs a list of Laptop objects.
     *
     * @return A List of Laptop objects representing the crawled laptop data.
     */
    public List<Laptop> getLaptops() {
        List<Laptop> laptops = new LinkedList<>();
        try (CSVReader laptopReader = getCSVReader("laptop.csv");
             CSVReader descriptionReader = getCSVReader("description.csv");
             CSVReader propertiesReader = getCSVReader("properties.csv");
             CSVReader reviewsReader = getCSVReader("reviews.csv")) {
            String[] laptopRow;
            while ((laptopRow = laptopReader.readNext()) != null) {
                String[] descriptionRow = descriptionReader.readNext();
                String[] propertiesRow = propertiesReader.readNext();
                List<String[]> reviews = new LinkedList<>();
                while (true) {
                    String[] reviewRow = reviewsReader.peek();
                    if (reviewRow == null) break;
                    if (!reviewRow[0].equals(laptopRow[0])) break;
                    reviews.add(reviewRow);
                    reviewsReader.readNext();
                }
                try {
                    laptops.add(parseLaptop(laptopRow, descriptionRow, propertiesRow, reviews));
                } catch (Exception e){
                    System.err.println("[ERROR] : An error occurred while trying to parse laptop #" + descriptionRow[1]);
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] : There was an error when accessing saving file");
            System.out.println(e.getMessage());
        }
        return laptops;
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
        CSVReader reader = new CSVReaderBuilder(new FileReader(resourceURL + filename))
                .withCSVParser(parser)
                .build();
        reader.readNext();
        return reader;
    }

    /**
     * Resets the CSV files and write the column headers.
     */
    void resetSave () {
        try (ICSVWriter laptopWriter = getCSVWriter("laptop.csv");
             ICSVWriter descriptionWriter = getCSVWriter("description.csv");
             ICSVWriter propertiesWriter = getCSVWriter("properties.csv");
             ICSVWriter reviewsWriter = getCSVWriter("reviews.csv")) {
            laptopWriter.writeNext(laptopColumn);
            descriptionWriter.writeNext(descriptionColumn);
            propertiesWriter.writeNext(propertiesColumn);
            reviewsWriter.writeNext(reviewColumn);
        } catch (Exception e){
            System.err.println("[ERROR] : There was an error when accessing saving file");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Saves a single laptop row to the laptop CSV file.
     *
     * @param laptopRow A String array containing laptop information.
     */
    void saveLaptopRow(String[] laptopRow) {
        try (ICSVWriter csvWriter = getCSVWriter("laptop.csv", true)) {
            csvWriter.writeNext(laptopRow);
        } catch (Exception e) {
            System.err.println("[ERROR] : There was an error when accessing saving file");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Saves a single description row to the description CSV file.
     *
     * @param descriptionRow A String array containing product description.
     */
    void saveDescriptionRow(String[] descriptionRow) {
        try (ICSVWriter csvWriter = getCSVWriter("description.csv", true)) {
            csvWriter.writeNext(descriptionRow);
        } catch (Exception e) {
            System.err.println("[ERROR] : There was an error when accessing saving file");
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
            System.err.println("[ERROR] : There was an error when accessing saving file");
            System.out.println(e.getMessage());
        }
    }

    void saveReviews(List<String[]> reviews) {
        try (ICSVWriter csvWriter = getCSVWriter("reviews.csv", true)) {
            for (String[] reviewRow : reviews) {
                csvWriter.writeNext(reviewRow);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] : There was an error when accessing saving file");
            System.out.println(e.getMessage());
        }
    }
}
