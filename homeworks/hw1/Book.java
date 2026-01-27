public class Book {
    private String title;
    private String author;
    private String isbn;
    private int copies;

    public Book(String title, String author, String isbn, int copies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.copies = copies;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getCopies() {
        return copies;
    }

    public void decreaseCopies() {
        if (copies > 0) {
            copies--;
        }
    }

    public void increaseCopies() {
        copies++;
    }

    @Override
    public String toString() {
        return title + " — " + author + " (ISBN: " + isbn + "), экземпляров: " + copies;
    }
}
