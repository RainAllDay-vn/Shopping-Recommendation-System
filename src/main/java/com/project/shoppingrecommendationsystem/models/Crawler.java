package com.project.shoppingrecommendationsystem.models;

import java.util.List;

public interface Crawler {

    /**
     * Crawls laptop information, removing existing save files (if exists) and fetching fresh data.
     */
    void crawlLaptop();

    /**
     * Retrieves a list of Laptop objects from the saved data files.
     * <p>
     * This method reads the previously saved laptop data from the respective files (e.g., CSV files containing
     * description and properties) and constructs a list of Laptop objects.
     *
     * @return A List of Laptop objects representing the crawled laptop data.
     */
    List<Laptop> getLaptops();
}
