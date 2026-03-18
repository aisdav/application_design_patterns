import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

interface IReport {
    String generate();
}

class SalesReport implements IReport {
    private List<String> data = new ArrayList<>();

    public SalesReport() {
        data.add("2023-01-01,Товар А,100");
        data.add("2023-01-02,Товар Б,200");
        data.add("2023-01-03,Товар А,150");
        data.add("2023-01-04,Товар В,300");
    }

    @Override
    public String generate() {
        return String.join(System.lineSeparator(), data);
    }
}

class UserReport implements IReport {
    private List<String> data = new ArrayList<>();

    public UserReport() {
        data.add("Иван,25,ivan@mail.com");
        data.add("Петр,30,petr@mail.com");
        data.add("Мария,28,maria@mail.com");
    }

    @Override
    public String generate() {
        return String.join(System.lineSeparator(), data);
    }
}

abstract class ReportDecorator implements IReport {
    protected IReport report;
    public ReportDecorator(IReport report) {
        this.report = report;
    }
}

class DateFilterDecorator extends ReportDecorator {
    private LocalDate start;
    private LocalDate end;
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DateFilterDecorator(IReport report, LocalDate start, LocalDate end) {
        super(report);
        this.start = start;
        this.end = end;
    }

    @Override
    public String generate() {
        String[] lines = report.generate().split(System.lineSeparator());
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            String[] parts = line.split(",");
            try {
                LocalDate date = LocalDate.parse(parts[0], fmt);
                if (!date.isBefore(start) && !date.isAfter(end)) {
                    sb.append(line).append(System.lineSeparator());
                }
            } catch (Exception e) {
                sb.append(line).append(System.lineSeparator());
            }
        }
        return sb.toString();
    }
}

class SortingDecorator extends ReportDecorator {
    private String criterion;

    public SortingDecorator(IReport report, String criterion) {
        super(report);
        this.criterion = criterion;
    }

    @Override
    public String generate() {
        List<String> lines = new ArrayList<>(List.of(report.generate().split(System.lineSeparator())));
        if (criterion.equals("date")) {
            lines.sort((a, b) -> {
                LocalDate da = LocalDate.parse(a.split(",")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate db = LocalDate.parse(b.split(",")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return da.compareTo(db);
            });
        } else if (criterion.equals("amount")) {
            lines.sort((a, b) -> {
                int amA = Integer.parseInt(a.split(",")[2]);
                int amB = Integer.parseInt(b.split(",")[2]);
                return Integer.compare(amA, amB);
            });
        }
        return String.join(System.lineSeparator(), lines);
    }
}

class CsvExportDecorator extends ReportDecorator {
    public CsvExportDecorator(IReport report) {
        super(report);
    }

    @Override
    public String generate() {
        return "CSV Export:" + System.lineSeparator() + report.generate();
    }
}

class PdfExportDecorator extends ReportDecorator {
    public PdfExportDecorator(IReport report) {
        super(report);
    }

    @Override
    public String generate() {
        return "PDF Export:" + System.lineSeparator() + report.generate();
    }
}

public class DecoratorDemo {
    public static void main(String[] args) {
        IReport sales = new SalesReport();
        sales = new DateFilterDecorator(sales, LocalDate.of(2023, 1, 2), LocalDate.of(2023, 1, 3));
        sales = new SortingDecorator(sales, "date");
        sales = new CsvExportDecorator(sales);

        System.out.println(sales.generate());

        IReport users = new UserReport();
        users = new PdfExportDecorator(users);
        System.out.println(users.generate());
    }
}
