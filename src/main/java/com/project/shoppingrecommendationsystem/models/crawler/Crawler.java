package com.project.shoppingrecommendationsystem.models.crawler;

import com.project.shoppingrecommendationsystem.models.Laptop;

import java.util.List;

public interface Crawler {

    /**
     * Crawls laptop information, removing existing save files (if exists) and fetching fresh data.
     */
    void crawlLaptops();

    /**
     * Crawls a limited number of laptop entries, removing existing save files (if exists) and fetching fresh data.
     *
     * @param limit The maximum number of laptops to crawl.
     */
    void crawlLaptops(int limit);

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
