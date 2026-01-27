public class Main {
    public static void main(String[] args) {
        Library library = new Library();

        Book book1 = new Book("1984", "Джордж Оруэлл", "ISBN001", 3);
        Book book2 = new Book("Мастер и Маргарита", "Булгаков", "ISBN002", 2);

        library.addBook(book1);
        library.addBook(book2);

        Reader reader1 = new Reader("Алиса", 1);
        Reader reader2 = new Reader("Боб", 2);

        library.registerReader(reader1);
        library.registerReader(reader2);

        library.lendBook("ISBN001", 1);
        library.returnBook("ISBN001");

        library.removeBook("ISBN002");
        library.removeReader(2);
    }
}
