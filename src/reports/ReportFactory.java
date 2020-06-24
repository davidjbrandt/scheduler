package reports;

import localization.Localizer;

public class ReportFactory {
    
    private final Localizer localizer;
    
    public ReportFactory(Localizer localizer) {
        this.localizer = localizer;
    }
    
    public ReportColumn newReportColumn(ColumnTitle title) {
        return new ReportColumn(localizer.translate(title.getValue()));
    }
}
