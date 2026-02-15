import java.util.Scanner;

interface Document {
    void open();
}

class Report implements Document {
    @Override
    public void open() {
        System.out.println("Открыт документ: Отчет (Report). Содержит аналитические данные.");
    }
}

class Resume implements Document {
    @Override
    public void open() {
        System.out.println("Открыт документ: Резюме (Resume). Содержит информацию о кандидате.");
    }
}

class Letter implements Document {
    @Override
    public void open() {
        System.out.println("Открыт документ: Письмо (Letter). Текстовое сообщение.");
    }
}

class Invoice implements Document {
    @Override
    public void open() {
        System.out.println("Открыт документ: Счет (Invoice). Содержит финансовые детали.");
    }
}

abstract class DocumentCreator {
    public abstract Document createDocument();

    public void someOperation() {
        Document doc = createDocument();
        System.out.println("Создатель выполняет подготовку...");
        doc.open();
    }
}

class ReportCreator extends DocumentCreator {
    @Override
    public Document createDocument() {
        return new Report();
    }
}

class ResumeCreator extends DocumentCreator {
    @Override
    public Document createDocument() {
        return new Resume();
    }
}

class LetterCreator extends DocumentCreator {
    @Override
    public Document createDocument() {
        return new Letter();
    }
}

class InvoiceCreator extends DocumentCreator {
    @Override
    public Document createDocument() {
        return new Invoice();
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Статическое тестирование ===");
        DocumentCreator creator = new ReportCreator();
        Document doc = creator.createDocument();
        doc.open();

        creator = new ResumeCreator();
        creator.createDocument().open();

        creator = new LetterCreator();
        creator.createDocument().open();

        creator = new InvoiceCreator();
        creator.createDocument().open();

        System.out.println("\n=== Динамическое создание ===");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nВведите тип документа (report/resume/letter/invoice) или 'exit' для выхода:");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("exit")) {
                break;
            }

            DocumentCreator dynamicCreator = getCreatorByType(input);
            if (dynamicCreator != null) {
                Document userDoc = dynamicCreator.createDocument();
                userDoc.open();
            } else {
                System.out.println("Неизвестный тип документа. Попробуйте снова.");
            }
        }
        scanner.close();
    }

    private static DocumentCreator getCreatorByType(String type) {
        switch (type) {
            case "report":
                return new ReportCreator();
            case "resume":
                return new ResumeCreator();
            case "letter":
                return new LetterCreator();
            case "invoice":
                return new InvoiceCreator();
            default:
                return null;
        }
    }
}
