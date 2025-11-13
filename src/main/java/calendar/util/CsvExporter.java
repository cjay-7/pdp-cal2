package calendar.util;

import calendar.model.EventInterface;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * CSV export utility for Google Calendar format.
 */
public final class CsvExporter {
  private CsvExporter() {
  }

  /**
   * Converts events to Google Calendar CSV format.
   *
   * @param events the events to export
   * @return CSV string representation
   */
  public static String toCsv(List<EventInterface> events) {
    StringBuilder csv = new StringBuilder();
    csv.append("Subject,Start Date,Start Time,End Date,End Time,");
    csv.append("All Day Event,Description,Location,Private\n");
    DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    DateTimeFormatter time = DateTimeFormatter.ofPattern("h:mm a");
    for (EventInterface e : events) {
      csv.append(escape(e.getSubject())).append(',')
          .append(e.getStartDateTime().toLocalDate().format(date)).append(',')
          .append(e.getStartDateTime().toLocalTime().format(time)).append(',')
          .append(e.getEndDateTime().toLocalDate().format(date)).append(',')
          .append(e.getEndDateTime().toLocalTime().format(time)).append(',')
          .append(e.isAllDayEvent() ? "True" : "False").append(',')
          .append(escape(e.getDescription().orElse(""))).append(',')
          .append(escape(e.getLocation().orElse(""))).append(',')
          .append(e.isPrivate() ? "True" : "False").append('\n');
    }
    return csv.toString();
  }

  /**
   * Escapes a CSV field.
   *
   * @param s the string to escape
   * @return escaped string
   */
  private static String escape(String s) {
    if (s == null) {
      return "";
    }
    if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
      return "\"" + s.replace("\"", "\"\"") + "\"";
    }
    return s;
  }
}
