import java.util.*;

abstract class FileSystemComponent {
    protected String name;

    public FileSystemComponent(String name) {
        this.name = name;
    }

    public void add(FileSystemComponent component) {
        throw new UnsupportedOperationException("Невозможно добавить компонент в файл");
    }

    public void remove(FileSystemComponent component) {
        throw new UnsupportedOperationException("Невозможно удалить компонент из файла");
    }

    public abstract void display(String indent);
    public abstract long getSize();
}

class File extends FileSystemComponent {
    private long size;

    public File(String name, long size) {
        super(name);
        this.size = size;
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "Файл: " + name + " (размер: " + size + " байт)");
    }

    @Override
    public long getSize() {
        return size;
    }
}

class Directory extends FileSystemComponent {
    private List<FileSystemComponent> children = new ArrayList<>();

    public Directory(String name) {
        super(name);
    }

    @Override
    public void add(FileSystemComponent component) {
        if (findChild(component.name) != null) {
            System.out.println("Предупреждение: компонент с именем \"" + component.name + "\" уже существует в папке \"" + this.name + "\". Добавление отменено.");
            return;
        }
        children.add(component);
    }

    @Override
    public void remove(FileSystemComponent component) {
        if (children.remove(component)) {
            System.out.println("Компонент \"" + component.name + "\" удалён из папки \"" + this.name + "\".");
        } else {
            System.out.println("Компонент \"" + component.name + "\" не найден в папке \"" + this.name + "\".");
        }
    }

    private FileSystemComponent findChild(String name) {
        for (FileSystemComponent child : children) {
            if (child.name.equals(name)) {
                return child;
            }
        }
        return null;
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "Папка: " + name);
        for (FileSystemComponent child : children) {
            child.display(indent + "  ");
        }
    }

    @Override
    public long getSize() {
        long totalSize = 0;
        for (FileSystemComponent child : children) {
            totalSize += child.getSize();
        }
        return totalSize;
    }
}

public class FileSystemDemo {
    public static void main(String[] args) {
        Directory root = new Directory("корень");

        Directory documents = new Directory("Документы");
        documents.add(new File("resume.pdf", 250_000));
        documents.add(new File("letter.txt", 12_000));

        Directory photos = new Directory("Фотографии");
        photos.add(new File("vacation.jpg", 1_500_000));
        photos.add(new File("family.jpg", 800_000));

        Directory work = new Directory("Работа");
        work.add(new File("report.docx", 350_000));
        work.add(new File("presentation.pptx", 2_000_000));

        Directory projects = new Directory("Проекты");
        projects.add(new File("project1.zip", 5_000_000));
        projects.add(new File("project2.zip", 7_200_000));
        work.add(projects);

        root.add(documents);
        root.add(photos);
        root.add(work);

        System.out.println("=== Попытка добавить дубликат ===");
        root.add(documents);

        System.out.println("\n=== Структура файловой системы ===");
        root.display("");

        System.out.println("\n=== Общий размер ===");
        System.out.println("Размер корневой папки: " + root.getSize() + " байт");
        System.out.println("Размер папки Документы: " + documents.getSize() + " байт");
        System.out.println("Размер папки Фотографии: " + photos.getSize() + " байт");
        System.out.println("Размер папки Работа: " + work.getSize() + " байт");
        System.out.println("Размер папки Проекты: " + projects.getSize() + " байт");

        System.out.println("\n=== Удаление файла из папки ===");
        work.remove(new File("presentation.pptx", 0)); 
        work.display("  ");

        work.remove(new File("old_file.txt", 0));
    }
}
