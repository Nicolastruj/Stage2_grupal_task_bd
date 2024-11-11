package org.ulpgc.apps;

import org.ulpgc.control.CrawlerController;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args){
        CrawlerController crawlerController = new CrawlerController();
        Path bookDatalakePath = Paths.get(System.getProperty("user.dir"), "BookDatalake");
        crawlerController.downloadingProcess(bookDatalakePath.toString());
    }
}
