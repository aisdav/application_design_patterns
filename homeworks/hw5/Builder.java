import java.util.ArrayList;
import java.util.List;

public class ReportBuilderDemo {

    public static class Style {
        private String font;
        private int fontSize;
        private String color;

        public Style(String font, int fontSize, String color) {
            this.font = font;
            this.fontSize = fontSize;
            this.color = color;
        }

        public String getFont() { return font; }
        public int getFontSize() { return fontSize; }
        public String getColor() { return color; }
    }

    public static class Report {
        private String header;
        private String content;
        private String footer;
        private Style style;

        public void setHeader(String header) { this.header = header; }
        public void setContent(String content) { this.content = content; }
        public void setFooter(String footer) { this.footer = footer; }
        public void setStyle(Style style) { this.style = style; }

        public String getHeader() { return header; }
        public String getContent() { return content; }
        public String getFooter() { return footer; }

        public String export() {
            StringBuilder sb = new StringBuilder();
            sb.append("HEADER: ").append(header).append("\n");
            sb.append("CONTENT: ").append(content).append("\n");
            sb.append("FOOTER: ").append(footer).append("\n");
            if (style != null) {
                sb.append("STYLE: font=").append(style.getFont())
                  .append(", size=").append(style.getFontSize())
                  .append(", color=").append(style.getColor());
            }
            return sb.toString();
        }
    }

    public interface IReportBuilder {
        void setHeader(String header);
        void setContent(String content);
        void setFooter(String footer);
        void setStyle(Style style);
        Report getReport();
    }

    public static class TextReportBuilder implements IReportBuilder {
        private Report report = new Report();

        public void setHeader(String header) { report.setHeader(header); }
        public void setContent(String content) { report.setContent(content); }
        public void setFooter(String footer) { report.setFooter(footer); }
        public void setStyle(Style style) { report.setStyle(style); }
        public Report getReport() { return report; }
    }

    public static class HtmlReportBuilder implements IReportBuilder {
        private Report report = new Report();

        public void setHeader(String header) { report.setHeader("<h1>" + header + "</h1>"); }
        public void setContent(String content) { report.setContent("<p>" + content + "</p>"); }
        public void setFooter(String footer) { report.setFooter("<footer>" + footer + "</footer>"); }
        public void setStyle(Style style) {
            report.setStyle(style);
        }
        public Report getReport() { return report; }
    }

    public static class ReportDirector {
        public void constructSimpleReport(IReportBuilder builder) {
            builder.setHeader("Simple Report");
            builder.setContent("This is a simple report content.");
            builder.setFooter("End of report.");
        }

        public void constructFullReport(IReportBuilder builder, Style style) {
            builder.setStyle(style);
            builder.setHeader("Full Report");
            builder.setContent("This is the main content of the full report.");
            builder.setFooter("Confidential");
        }
    }

    public static void main(String[] args) {
        ReportDirector director = new ReportDirector();
        Style style = new Style("Arial", 12, "black");

        IReportBuilder textBuilder = new TextReportBuilder();
        director.constructFullReport(textBuilder, style);
        Report textReport = textBuilder.getReport();
        System.out.println("=== Text Report ===");
        System.out.println(textReport.export());

        IReportBuilder htmlBuilder = new HtmlReportBuilder();
        director.constructSimpleReport(htmlBuilder);
        Report htmlReport = htmlBuilder.getReport();
        htmlReport.setContent("<p><b>Updated</b> content</p>");
        System.out.println("\n=== HTML Report (modified) ===");
        System.out.println(htmlReport.export());

        IReportBuilder xmlBuilder = new IReportBuilder() {
            private StringBuilder xml = new StringBuilder();
            public void setHeader(String header) { xml.append("<header>").append(header).append("</header>\n"); }
            public void setContent(String content) { xml.append("<content>").append(content).append("</content>\n"); }
            public void setFooter(String footer) { xml.append("<footer>").append(footer).append("</footer>\n"); }
            public void setStyle(Style style) { }
            public Report getReport() {
                return new Report() {
                    @Override
                    public String export() {
                        return "<report>\n" + xml.toString() + "</report>";
                    }
                };
            }
        };
        director.constructSimpleReport(xmlBuilder);
        System.out.println("\n=== XML Report ===");
        System.out.println(xmlBuilder.getReport().export());
    }
}
