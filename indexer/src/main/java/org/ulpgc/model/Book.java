package org.ulpgc.model;

public class Book {
    private String bookId;
    private String title;
    private String author;
    private String URL;
    private String content;

    public Book(String bookId, String title, String author, String URL, String content) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.URL = URL;
        this.content = content;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId='" + bookId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", URL='" + URL + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
