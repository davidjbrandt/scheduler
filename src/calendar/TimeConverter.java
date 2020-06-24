package calendar;

import java.sql.Timestamp;
import java.time.*;

public class TimeConverter {
    
    private final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");
    private final ZoneId LOCAL_ZONE_ID = ZoneId.systemDefault();
    
    public LocalDateTime utcToLocal(Timestamp timestamp) {
        ZonedDateTime utcZDT = ZonedDateTime.of(timestamp.toLocalDateTime(), UTC_ZONE_ID);
        ZonedDateTime localZDT = utcZDT.withZoneSameInstant(LOCAL_ZONE_ID);
        return localZDT.toLocalDateTime();
    }
    
    public String nowInUTC() {
        return localToUTC(LocalDateTime.now());
    }
    
    public String localToUTC(LocalDateTime localDateTime) {
        ZonedDateTime localZDT = ZonedDateTime.of(localDateTime, LOCAL_ZONE_ID);
        ZonedDateTime utcZDT = localZDT.withZoneSameInstant(UTC_ZONE_ID);
        LocalDateTime utcDateTime = utcZDT.toLocalDateTime();
        LocalDate utcDate = utcDateTime.toLocalDate();
        LocalTime utcTime = utcDateTime.toLocalTime();
        return utcDate.toString() + " " + utcTime.toString();
    }
}
