package reports;

import localization.Localizer;

import java.util.ArrayList;
import java.util.function.Supplier;

public class Report {
    
    private final String name;
    private final Supplier<ArrayList<ReportColumn>> resultSupplier;
    private final Localizer localizer;
    
    public Report(String name, Supplier<ArrayList<ReportColumn>> resultSupplier, Localizer localizer) {
        this.name = name;
        this.resultSupplier = resultSupplier;
        this.localizer = localizer;
    }
    
    public String getResults() {
        return formatResults(resultSupplier.get());
    }
    
    private String formatResults(ArrayList<ReportColumn> columns) {
        return formatTitleRow(columns) + formatTitleUnderline(columns) + formatDataLines(columns);
    }
    
    private String formatTitleRow(ArrayList<ReportColumn> columns) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|");
        for (ReportColumn column : columns) {
            stringBuilder.append(column.getTitle());
        }
        return stringBuilder.toString();
    }
    
    private String formatTitleUnderline(ArrayList<ReportColumn> columns) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n|");
        for (ReportColumn column : columns) {
            stringBuilder.append(column.getLine());
        }
        return stringBuilder.toString();
    }
    
    private String formatDataLines(ArrayList<ReportColumn> columns) {
        StringBuilder stringBuilder = new StringBuilder();
        while (columns.get(0).hasNext()) {
            stringBuilder.append("\n|");
            for (ReportColumn column : columns) {
                stringBuilder.append(column.getNext());
            }
        }
        return stringBuilder.toString();
    }
    
    @Override
    public String toString() {
        return localizer.translate(name);
    }
}
