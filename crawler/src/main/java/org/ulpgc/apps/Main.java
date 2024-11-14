package org.ulpgc.apps;

import org.ulpgc.control.CrawlerController;

public class Main {
    public static void main(String[] args) {
        String datamartPath = "./Datalake";
        String metadataPath = "./metadata.csv";
        CrawlerController.periodicTask(20, datamartPath, metadataPath);
    }
}
