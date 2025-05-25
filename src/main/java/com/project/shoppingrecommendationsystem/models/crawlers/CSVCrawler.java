package com.project.shoppingrecommendationsystem.models.crawlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import com.project.shoppingrecommendationsystem.models.Product;

import java.io.*;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.*;

public abstract class CSVCrawler implements Crawler{
    protected static final CSVParser PARSER = new CSVParserBuilder()
            .withSeparator(',')
            .withEscapeChar('\\')
            .withQuoteChar('\'')
            .build();
    protected static final ObjectMapper MAPPER = new ObjectMapper();
    protected String resourceURL;
    protected String[] productColumn = new String[]{"local_id"};
    protected String[] descriptionColumn = new String[]{"local_id", "type", "description"};
    protected String[] propertyColumn = new String[]{"local_id"};
    protected String[] reviewColumn = new String[]{"local_id", "created", "content", "score", "username"};
    protected final Map<String, Integer> productsMap = new HashMap<>();
    protected final Map<String, Integer> descriptionsMap = new HashMap<>();
    protected final Map<String, Integer> propertiesMap = new HashMap<>();
    protected final Map<String, Integer> reviewsMap = new HashMap<>();

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new LinkedList<>();
        try (CSVReader productReader = getCSVReader(resourceURL+"products.csv");
             CSVReader descriptionReader = getCSVReader(resourceURL+"descriptions.csv");
             CSVReader propertiesReader = getCSVReader(resourceURL+"properties.csv");
             CSVReader reviewsReader = getCSVReader(resourceURL+"reviews.csv")) {
            List<List<String[]>> productData;
            while ((productData = readProduct(productReader, descriptionReader, propertiesReader, reviewsReader)) != null){
                try {
                    products.add(parseProduct(
                            productData.get(0).getFirst(),
                            productData.get(1),
                            productData.get(2).getFirst(),
                            productData.get(3)));
                } catch (Exception e){
                    System.err.println("[ERROR] : An error occurred while trying to parse product #" + productData.getFirst().getFirst()[0]);
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] : There was an error when accessing saving file");
            System.out.println(e.getMessage());
        }
        return products;
    }

    protected abstract Product parseProduct(String[] productRow, List<String[]> descriptions, String[] propertiesRow, List<String[]> reviews);

    protected void initializeMap() {
        for (int i = 0; i < productColumn.length; i++) {
            productsMap.put(productColumn[i], i);
        }
        for (int i = 0; i < descriptionColumn.length; i++) {
            descriptionsMap.put(descriptionColumn[i], i);
        }
        for (int i = 0; i < propertyColumn.length; i++) {
            propertiesMap.put(propertyColumn[i], i);
        }
        for (int i = 0; i < reviewColumn.length; i++) {
            reviewsMap.put(reviewColumn[i], i);
        }
        File resourceDir = new File(resourceURL+"images/");
        try {
            boolean success = resourceDir.mkdirs();
            assert success;
        } catch (Exception e) {
            throw new RuntimeException("Cannot create resource directory: " + resourceURL);
        }
    }

    protected List<List<String[]>> readProduct (CSVReader productReader, CSVReader descriptionReader, CSVReader propertiesReader, CSVReader reviewsReader) {
        List<List<String[]>> result = new ArrayList<>();
        try {
            String[] productRow = productReader.readNext();
            if (productRow == null) {
                return null;
            }
            result.add(new ArrayList<>());
            result.getLast().add(productRow);
            List<String[]> descriptions = new LinkedList<>();
            while (true) {
                String[] descriptionRow = descriptionReader.peek();
                if (descriptionRow == null) break;
                if (!descriptionRow[0].equals(productRow[0])) break;
                descriptions.add(new String[]{descriptionRow[1], descriptionRow[2]});
                descriptionReader.readNext();
            }
            result.add(descriptions);
            String[] propertiesRow = propertiesReader.readNext();
            result.add(new ArrayList<>());
            result.getLast().add(propertiesRow);
            List<String[]> reviews = new LinkedList<>();
            while (true) {
                String[] reviewRow = reviewsReader.peek();
                if (reviewRow == null) break;
                if (!reviewRow[0].equals(productRow[0])) break;
                reviews.add(new String[]{reviewRow[1], reviewRow[2], reviewRow[3], reviewRow[4]});
                reviewsReader.readNext();
            }
            result.add(reviews);
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    protected ICSVWriter getCSVWriter (String filepath) throws IOException {
        return getCSVWriter(filepath, false);
    }

    protected ICSVWriter getCSVWriter (String filename, boolean append) throws IOException {
        return new CSVWriterBuilder(new FileWriter(resourceURL+filename, append))
                .withParser(PARSER)
                .build();
    }

    protected CSVReader getCSVReader (String filepath) throws IOException, CsvValidationException {
        File file = new File(filepath);
        if (!file.exists() && !file.createNewFile()) {
            System.out.println("[ERROR] : An error occurred while creating missing file ("+filepath+")");
        }
        CSVReader reader = new CSVReaderBuilder(new FileReader(filepath))
                .withCSVParser(PARSER)
                .build();
        reader.readNext();
        return reader;
    }

    protected void resetSave () {
        try (ICSVWriter phoneWriter = getCSVWriter("products.csv");
             ICSVWriter descriptionWriter = getCSVWriter("descriptions.csv");
             ICSVWriter propertiesWriter = getCSVWriter("properties.csv");
             ICSVWriter reviewsWriter = getCSVWriter("reviews.csv")) {
            phoneWriter.writeNext(productColumn);
            descriptionWriter.writeNext(descriptionColumn);
            propertiesWriter.writeNext(propertyColumn);
            reviewsWriter.writeNext(reviewColumn);
        } catch (Exception e){
            System.err.println("[ERROR] : There was an error when accessing saving file");
            System.out.println(e.getMessage());
        }
    }

    protected synchronized void save(String[] phoneRow, List<String[]> descriptions, String[] propertiesRow, List<String[]> reviews) {
        saveProductRow(phoneRow);
        saveDescriptions(descriptions);
        savePropertiesRow(propertiesRow);
        saveReviews(reviews);
    }

    protected void saveProductRow(String[] productRow) {
        try (ICSVWriter csvWriter = getCSVWriter("products.csv", true)) {
            csvWriter.writeNext(productRow);
        } catch (Exception e) {
            System.err.println("[ERROR] : There was an error when saving to products file");
            System.out.println(e.getMessage());
        }
    }

    protected void saveDescriptions(List<String[]> descriptions) {
        try (ICSVWriter csvWriter = getCSVWriter("descriptions.csv", true)) {
            for (String[] descriptionRow : descriptions) {
                csvWriter.writeNext(descriptionRow);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] : There was an error when saving to descriptions file");
            System.out.println(e.getMessage());
        }
    }

    protected void savePropertiesRow(String[] propertiesRow) {
        try (ICSVWriter csvWriter = getCSVWriter("properties.csv", true)) {
            csvWriter.writeNext(propertiesRow);
        } catch (Exception e) {
            System.err.println("[ERROR] : There was an error when saving to properties file");
            System.out.println(e.getMessage());
        }
    }

    protected void saveReviews(List<String[]> reviews) {
        try (ICSVWriter csvWriter = getCSVWriter("reviews.csv", true)) {
            csvWriter.writeAll(reviews);
        } catch (Exception e) {
            System.err.println("[ERROR] : There was an error when saving to reviews file");
            System.out.println(e.getMessage());
        }
    }

    protected String downloadImage (String url, String id) {
        url = url.replace(" ", "%20");
        String extension = url.substring(url.lastIndexOf(".") + 1);
        String outputPath = resourceURL + "images/" + id + "." + extension;
        try (ReadableByteChannel in = Channels.newChannel(new URI(url).toURL().openStream());
             FileOutputStream out = new FileOutputStream(outputPath)) {
            FileChannel channel = out.getChannel();
            channel.transferFrom(in, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            System.err.println("[ERROR] : An error occurred while downloading image");
            System.out.println(e.getMessage());
            File outputFile = new File(outputPath);
            if(outputFile.exists()&&!outputFile.delete()) {
                System.err.println("[ERROR] : Can not delete junk image");
            }
            return url;
        }
        return outputPath;
    }
}
