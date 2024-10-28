package org.ulpgc.model;

public class Book {
    private String id;
    private String name;
    private String author;
    private String url;

    public Book(String id, String name, String author, String url) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.url = url;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAuthor() { return author; }
    public String getUrl() { return url; }
}