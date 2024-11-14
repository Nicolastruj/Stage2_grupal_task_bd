package org.ulpgc.implementations;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.ulpgc.exceptions.CrawlerException;
import org.ulpgc.ports.ReaderFromWebInterface;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReaderFromWeb implements ReaderFromWebInterface {

    private static final int CONNECTION_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 15000;

    @Override
    public InputStream downloadBookStream(int bookId) throws CrawlerException {
        String bookUrl = "https://www.gutenberg.org/files/" + bookId + "/" + bookId + "-0.txt";
        try {
            URL url = new URL(bookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return connection.getInputStream();
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                return null; // Handle 404 as a normal case
            } else {
                throw new CrawlerException("HTTP error code: " + responseCode); // For other HTTP errors
            }
        } catch (IOException e) {
            throw new CrawlerException("Error connecting to URL: " + bookUrl, e);
        }
    }

    @Override
    public String[] getTitleAndAuthor(int bookId) throws CrawlerException {
        String bookUrl = "https://www.gutenberg.org/ebooks/" + bookId;
        try {
            Document doc = Jsoup.connect(bookUrl).get();
            Element h1Element = doc.selectFirst("h1");

            if (h1Element != null) {
                String titleAndAuthor = h1Element.text();
                String[] parts = titleAndAuthor.split(" by ", 2);
                String title = parts[0].replaceAll("[\\/:*?\"<>|]", "").replace(",", ";");
                String author = (parts.length > 1 ? parts[1] : "Unknown Author").replaceAll("[\\/:*?\"<>|]", "").replace(",", ";");

                return new String[]{title, author};
            } else {
                throw new CrawlerException("Title and author not found.");
            }
        } catch (IOException e) {
            throw new CrawlerException("Error retrieving title and author: " + e.getMessage(), e);
        }
    }
}



