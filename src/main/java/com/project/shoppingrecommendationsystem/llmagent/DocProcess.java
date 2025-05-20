package com.project.shoppingrecommendationsystem.llmagent;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.*;


@Component
public class DocProcess {

    /**
     * Reads the laptop + description CSVs and returns split documents.
     *
     * @param laptopCsv      path to laptop.csv
     * @param descriptionCsv path to description.csv
     * @return List<Document> ready for embedding / vector-store upload
     */
    public List<Document> loadAndSplit(Path laptopCsv, Path descriptionCsv) throws Exception {
        Map<String, Map<String, Object>> metadataMap = new HashMap<>();

        // Read laptop data with manual header handling
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(laptopCsv.toFile()))
                .withSkipLines(0)
                .build()) {

            String[] headers = reader.readNext();
            String[] row;
            while ((row = reader.readNext()) != null) {
                Map<String, String> rowMap = new HashMap<>();
                for (int i = 0; i < Math.min(headers.length, row.length); i++) {
                    rowMap.put(headers[i], row[i]);
                }

                String dataId = rowMap.getOrDefault("data-id", "").trim();
                if (dataId.isEmpty()) continue;

                Map<String, Object> metadata = new HashMap<>();
                metadata.put("data-id", dataId);
                metadata.put("data-name", rowMap.getOrDefault("data-name", "").trim());
                metadata.put("data-price", rowMap.getOrDefault("data-price", "").trim());

                metadataMap.put(dataId, metadata);
            }
        }

        List<Document> rawDocs = new ArrayList<>();

        // Read description data with manual header handling
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(descriptionCsv.toFile()))
                .withSkipLines(0)
                .build()) {

            String[] headers = reader.readNext();
            String[] row;
            while ((row = reader.readNext()) != null) {
                Map<String, String> rowMap = new HashMap<>();
                for (int i = 0; i < Math.min(headers.length, row.length); i++) {
                    rowMap.put(headers[i], row[i]);
                }

                String dataId = rowMap.getOrDefault("data-id", "").trim();
                String description = rowMap.getOrDefault("description", "").trim();

                if (dataId.isEmpty() || description.isEmpty()) continue;

                Map<String, Object> metadata = metadataMap.get(dataId);
                if (metadata != null) {
                    rawDocs.add(new Document(description, metadata));
                }
            }
        }

        TokenTextSplitter splitter =
                TokenTextSplitter.builder()
                        .withChunkSize(700)
                        .withKeepSeparator(true)
                        .build();

        return splitter.apply(rawDocs);
    }
}