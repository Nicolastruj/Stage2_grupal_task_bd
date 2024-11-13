package org.ulpgc.model;

public class Book {
    private String bookId;
    private String content;

    public Book(String bookId, String content) {
        this.bookId = bookId;
        this.content = content;
    }

    public String getBookId() {
        return bookId;
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
                ", content='" + content + '\'' +
                '}';
    }
}
