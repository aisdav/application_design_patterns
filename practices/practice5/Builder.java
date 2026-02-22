import java.util.ArrayList;
import java.util.List;

public class ReportBuilderDemo {

    public static class ReportStyle {
        private String backgroundColor;
        private String fontColor;
        private int fontSize;

        public ReportStyle(String backgroundColor, String fontColor, int fontSize) {
            this.backgroundColor = backgroundColor;
            this.fontColor = fontColor;
            this.fontSize = fontSize;
        }

        public String getBackgroundColor() { return backgroundColor; }
        public String getFontColor() { return fontColor; }
        public int getFontSize() { return fontSize; }
    }

    public interface IReportBuilder {
        void setHeader(String header);
        void setContent(String content);
        void setFooter(String footer);
        void addSection(String sectionName, String sectionContent);
        void setStyle(ReportStyle style);
        Report getReport();
    }
    public static class Report {
        private String header;
        private String content;
        private String footer;
        private List<Section> sections = new ArrayList<>();
        private ReportStyle style;

        public static class Section {
            public String name;
            public String content;
            public Section(String name, String content) {
                this.name = name;
                this.content = content;
            }
        }

        public void setHeader(String header) { this.header = header; }
        public void setContent(String content) { this.content = content; }
        public void setFooter(String footer) { this.footer = footer; }
        public void addSection(Section section) { sections.add(section); }
        public void setStyle(ReportStyle style) { this.style = style; }

        public String export() {
            StringBuilder sb = new StringBuilder();
            sb.append("Header: ").append(header).append("\n");
            sb.append("Content: ").append(content).append("\n");
            for (Section s : sections) {
                sb.append("Section [").append(s.name).append("]: ").append(s.content).append("\n");
            }
            sb.append("Footer: ").append(footer).append("\n");
            sb.append("Style: bg=").append(style.getBackgroundColor())
              .append(", font=").append(style.getFontColor())
              .append(", size=").append(style.getFontSize());
            return sb.toString();
        }
    }
    public static class TextReportBuilder implements IReportBuilder {
        private Report report = new Report();
        public void setHeader(String header) { report.setHeader(header); }
        public void setContent(String content) { report.setContent(content); }
        public void setFooter(String footer) { report.setFooter(footer); }
        public void addSection(String sectionName, String sectionContent) {
            report.addSection(new Report.Section(sectionName, sectionContent));
        }
        public void setStyle(ReportStyle style) { report.setStyle(style); }
        public Report getReport() { return report; }
    }

    public static class HtmlReportBuilder implements IReportBuilder {
        private StringBuilder html = new StringBuilder();
        private ReportStyle style;
        public void setHeader(String header) { html.append("<h1>").append(header).append("</h1>\n"); }
        public void setContent(String content) { html.append("<p>").append(content).append("</p>\n"); }
        public void setFooter(String footer) { html.append("<footer>").append(footer).append("</footer>\n"); }
        public void addSection(String sectionName, String sectionContent) {
            html.append("<h2>").append(sectionName).append("</h2>\n");
            html.append("<p>").append(sectionContent).append("</p>\n");
        }
        public void setStyle(ReportStyle style) { this.style = style; }
        public Report getReport() {
            String fullHtml = "<html><head><style>body { background-color: " + style.getBackgroundColor() +
                    "; color: " + style.getFontColor() + "; font-size: " + style.getFontSize() + "px; }</style></head><body>"
                    + html.toString() + "</body></html>";
            return new Report() {
                @Override
                public String export() {
                    return fullHtml;
                }
            };
        }
    }
    public static class PdfReportBuilder implements IReportBuilder {
        private StringBuilder content = new StringBuilder();
        private ReportStyle style;
        public void setHeader(String header) { content.append("Header: ").append(header).append("\n"); }
        public void setContent(String content) { this.content.append("Content: ").append(content).append("\n"); }
        public void setFooter(String footer) { this.content.append("Footer: ").append(footer).append("\n"); }
        public void addSection(String sectionName, String sectionContent) {
            content.append("Section ").append(sectionName).append(": ").append(sectionContent).append("\n");
        }
        public void setStyle(ReportStyle style) { this.style = style; }
        public Report getReport() {
            return new Report() {
                @Override
                public String export() {
                    return "[PDF GENERATION]\n" + content.toString() + "\nStyle: " + style.getBackgroundColor() + "/" + style.getFontColor();
                }
            };
        }
    }

    public static class ReportDirector {
        public void constructReport(IReportBuilder builder, ReportStyle style) {
            builder.setStyle(style);
            builder.setHeader("Monthly Report");
            builder.setContent("This is the main content of the report.");
            builder.addSection("Sales", "Sales increased by 20%");
            builder.addSection("Expenses", "Expenses decreased by 5%");
            builder.setFooter("Confidential - For internal use only");
        }
    }

    public static void main(String[] args) {
        ReportDirector director = new ReportDirector();
        ReportStyle style = new ReportStyle("white", "black", 12);

        IReportBuilder textBuilder = new TextReportBuilder();
        director.constructReport(textBuilder, style);
        System.out.println("=== Text Report ===\n" + textBuilder.getReport().export());

        IReportBuilder htmlBuilder = new HtmlReportBuilder();
        director.constructReport(htmlBuilder, style);
        System.out.println("\n=== HTML Report ===\n" + htmlBuilder.getReport().export());

        IReportBuilder pdfBuilder = new PdfReportBuilder();
        director.constructReport(pdfBuilder, style);
        System.out.println("\n=== PDF Report ===\n" + pdfBuilder.getReport().export());
    }
}
