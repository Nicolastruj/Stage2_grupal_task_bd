package org.ulpgc.indexer.model;

public class Book {
    private String title;
    private String author;
    private String URL;
    private String content;

    // Constructor
    public Book(String title, String author, String URL, String content) {
        this.title = title;
        this.author = author;
        this.URL = URL;
        this.content = content;
    }

    // Getter para el título
    public String getTitle() {
        return title;
    }

    // Setter para el título
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter para el autor
    public String getAuthor() {
        return author;
    }

    // Setter para el autor
    public void setAuthor(String author) {
        this.author = author;
    }

    // Getter para la URL
    public String getURL() {
        return URL;
    }

    // Setter para la URL
    public void setURL(String URL) {
        this.URL = URL;
    }

    // Getter para el contenido
    public String getContent() {
        return content;
    }

    // Setter para el contenido
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", URL='" + URL + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

