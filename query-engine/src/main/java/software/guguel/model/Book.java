package software.guguel.model;

public class Book {
    private final String id;
    private final String name;
    private final String author;
    private final String url;

    public Book(String id, String name, String author, String url) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }
}