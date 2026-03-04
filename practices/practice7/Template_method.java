import java.util.*;
import java.time.LocalDateTime;

public class TemplateReportDemo {

    static abstract class ReportGenerator {
        protected final Scanner in;
        protected final List<String> data;
        protected final Logger log;

        protected String reportTitle = "Monthly Report";
        protected String output = "";

        protected ReportGenerator(List<String> data, Scanner in, Logger log) {
            this.data = Objects.requireNonNull(data);
            this.in = Objects.requireNonNull(in);
            this.log = Objects.requireNonNull(log);
        }

        public final void generateReport() {
            log.step("start", name());
            validateInput();
            preProcessHook();

            String header = buildHeader();          
            String body = formatBody(data);         
            String footer = buildFooter();        

            output = assemble(header, body, footer);
            postProcessHook();

            if (customerWantsSave()) {              
                save(output);                      
            } else if (customerWantsEmail()) {   
                email(output);                    
            } else {
                log.step("skip", "User chose neither save nor email");
            }

            log.step("done", name());
            System.out.println("\n--- " + name() + " OUTPUT ---\n" + output + "\n");
        }

        protected void validateInput() {
            log.step("validateInput", "Checking data not empty");
            if (data.isEmpty()) throw new IllegalArgumentException("Data is empty");
        }

        protected abstract String buildHeader();
        protected abstract String formatBody(List<String> data);

        protected String buildFooter() {
            return "\nGenerated at: " + LocalDateTime.now();
        }

        protected String assemble(String header, String body, String footer) {
            log.step("assemble", "Combining header/body/footer");
            return header + "\n" + body + footer;
        }

        protected void preProcessHook() { log.step("preProcessHook", "none"); }
        protected void postProcessHook() { log.step("postProcessHook", "none"); }

        protected boolean customerWantsSave() {
            return askYesNo("Do you want to SAVE the " + name() + " report? (y/n): ");
        }

        protected boolean customerWantsEmail() {
            return askYesNo("Do you want to EMAIL the " + name() + " report? (y/n): ");
        }

        protected void save(String content) {
            log.step("save", "Default save: simulated save to file");
            System.out.println("💾 Saved (" + name() + "): " + content.length() + " chars");
        }

        protected void email(String content) {
            log.step("email", "Simulated email send");
            System.out.println("📧 Emailed (" + name() + "): " + content.length() + " chars");
        }

        protected boolean askYesNo(String prompt) {
            while (true) {
                System.out.print(prompt);
                String s = in.nextLine().trim().toLowerCase(Locale.ROOT);
                if (s.equals("y") || s.equals("yes")) return true;
                if (s.equals("n") || s.equals("no")) return false;
                log.step("inputError", "Invalid input: '" + s + "'. Expected y/n.");
                System.out.println("Please enter y/yes or n/no.");
            }
        }

        protected String name() { return getClass().getSimpleName(); }
    }


    static class PdfReport extends ReportGenerator {
        PdfReport(List<String> data, Scanner in, Logger log) { super(data, in, log); }

        protected void preProcessHook() {
            log.step("preProcessHook", "PDF: applying font setup (simulated)");
        }

        @Override protected String buildHeader() {
            log.step("buildHeader", "PDF header");
            return "=== PDF REPORT: " + reportTitle + " ===";
        }

        @Override protected String formatBody(List<String> data) {
            log.step("formatBody", "PDF: bullet formatting");
            StringBuilder sb = new StringBuilder();
            for (String row : data) sb.append("• ").append(row).append("\n");
            return sb.toString();
        }

        @Override protected String buildFooter() {
            log.step("buildFooter", "PDF footer");
            return "\n[PDF Footer] " + LocalDateTime.now();
        }
    }

    static class ExcelReport extends ReportGenerator {
        ExcelReport(List<String> data, Scanner in, Logger log) { super(data, in, log); }

        @Override protected String buildHeader() {
            log.step("buildHeader", "Excel header");
            return "EXCEL_SHEET\t" + reportTitle;
        }

        @Override protected String formatBody(List<String> data) {
            log.step("formatBody", "Excel: tabular (TSV) rows");
            StringBuilder sb = new StringBuilder();
            sb.append("Row\tValue\n");
            int i = 1;
            for (String row : data) sb.append(i++).append("\t").append(row).append("\n");
            return sb.toString();
        }

        @Override protected void save(String content) {
            log.step("save", "Excel: simulated .xlsx save with validation");
            if (!content.startsWith("EXCEL_SHEET")) {
                throw new IllegalStateException("Excel content header missing");
            }
            System.out.println("💾 Saved EXCEL (.xlsx simulated). Rows: " + data.size());
        }
    }

    static class HtmlReport extends ReportGenerator {
        HtmlReport(List<String> data, Scanner in, Logger log) { super(data, in, log); }

        @Override protected String buildHeader() {
            log.step("buildHeader", "HTML header");
            return "<html><head><title>" + reportTitle + "</title></head><body><h1>" + reportTitle + "</h1>";
        }

        @Override protected String formatBody(List<String> data) {
            log.step("formatBody", "HTML: <ul>");
            StringBuilder sb = new StringBuilder("<ul>");
            for (String row : data) sb.append("<li>").append(escape(row)).append("</li>");
            sb.append("</ul>");
            return sb.toString();
        }

        @Override protected String buildFooter() {
            log.step("buildFooter", "HTML footer");
            return "<footer><small>Generated at " + LocalDateTime.now() + "</small></footer></body></html>";
        }

        private String escape(String s) {
            return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }

    static class CsvReport extends ReportGenerator {
        CsvReport(List<String> data, Scanner in, Logger log) { super(data, in, log); }

        @Override protected String buildHeader() {
            log.step("buildHeader", "CSV header");
            return "title," + quote(reportTitle) + "\nindex,value";
        }

        @Override protected String formatBody(List<String> data) {
            log.step("formatBody", "CSV rows");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.size(); i++) {
                sb.append("\n").append(i + 1).append(",").append(quote(data.get(i)));
            }
            return sb.toString();
        }

        @Override protected String buildFooter() {
            log.step("buildFooter", "CSV footer");
            return "\n# generated_at=" + LocalDateTime.now();
        }

        private String quote(String s) {
            String t = s.replace("\"", "\"\"");
            return "\"" + t + "\"";
        }
    }

    static class Logger {
        void step(String step, String msg) {
            System.out.println("🧾 [" + step + "] " + msg);
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Logger log = new Logger();

        List<String> sample = List.of(
                "Revenue: 120000",
                "Costs: 70000",
                "Profit: 50000",
                "Top product: A-17"
        );

        List<ReportGenerator> reports = List.of(
                new PdfReport(sample, in, log),
                new ExcelReport(sample, in, log),
                new HtmlReport(sample, in, log),
                new CsvReport(sample, in, log)
        );

        for (ReportGenerator r : reports) {
            try {
                System.out.println("\n==============================");
                System.out.println("Generating: " + r.getClass().getSimpleName());
                System.out.println("==============================");
                r.generateReport();
            } catch (Exception e) {
                System.out.println("❌ Error while generating " + r.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }
}
