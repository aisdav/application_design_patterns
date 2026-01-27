public class Reader {
    private String name;
    private int readerId;

    public Reader(String name, int readerId) {
        this.name = name;
        this.readerId = readerId;
    }

    public int getReaderId() {
        return readerId;
    }

    @Override
    public String toString() {
        return "Читатель: " + name + ", ID: " + readerId;
    }
}
