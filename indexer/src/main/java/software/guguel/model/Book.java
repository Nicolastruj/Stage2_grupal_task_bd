package software.guguel.model;

public record Book(String bookId, String content) {

    @Override
    public String toString() {
        return "Book{" +
                "bookId='" + bookId + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}