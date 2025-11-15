package calendar.utils;

import calendar.model.EventInterface;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for exporting events to iCal (RFC 5545) format.
 * Generates .ical files compatible with Google Calendar and other calendar applications.
 *
 * <p>DESIGN RATIONALE:
 * - Follows RFC 5545 specification for iCalendar format
 * - Converts LocalDateTime to UTC for DTSTART/DTEND (required by spec)
 * - Generates unique UIDs for each event
 * - Includes DTSTAMP (creation timestamp)
 * - Handles optional fields (description, location)
 * - Escapes special characters in text fields
 * - Line folding at 75 characters as per RFC 5545
 *
 * <p>Example output:
 * <pre>
 * BEGIN:VCALENDAR
 * VERSION:2.0
 * PRODID:-
 * BEGIN:VEVENT
 * UID:event-id@calendar.app
 * DTSTAMP:20250611T120000Z
 * DTSTART:20250601T090000Z
 * DTEND:20250601T100000Z
 * SUMMARY:Team Meeting
 * DESCRIPTION:Discuss project status
 * LOCATION:Conference Room A
 * END:VEVENT
 * END:VCALENDAR
 * </pre>
 */
public class IcalExporter {

  /**
   * Maximum line length for iCal format as specified by RFC 5545.
   * Lines longer than this must be folded with continuation.
   */
  private static final int RFC5545_LINE_FOLD_WIDTH = 75;

  private static final DateTimeFormatter ICAL_DATETIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

  /**
   * Private constructor to prevent instantiation of utility class.
   */
  private IcalExporter() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  /**
   * Exports a list of events to iCal format.
   *
   * @param events the list of events to export
   * @param calendarName the name of the calendar (used in PRODID)
   * @param timezone the timezone of the calendar
   * @return the iCal formatted string
   */
  public static String toIcal(List<EventInterface> events, String calendarName, ZoneId timezone) {
    StringBuilder ical = new StringBuilder();

    ical.append("BEGIN:VCALENDAR\r\n");
    ical.append("VERSION:2.0\r\n");
    ical.append("PRODID:-//Calendar//").append(escapeText(calendarName)).append("//EN\r\n");
    ical.append("CALSCALE:GREGORIAN\r\n");
    ical.append("METHOD:PUBLISH\r\n");

    for (EventInterface event : events) {
      ical.append(formatEvent(event, timezone));
    }

    ical.append("END:VCALENDAR\r\n");

    return ical.toString();
  }

  /**
   * Formats a single event in iCal format.
   *
   * @param event the event to format
   * @param timezone the timezone of the calendar
   * @return the formatted VEVENT component
   */
  private static String formatEvent(EventInterface event, ZoneId timezone) {
    StringBuilder vevent = new StringBuilder();

    vevent.append("BEGIN:VEVENT\r\n");

    vevent.append("UID:").append(event.getId().toString()).append("@calendar.app\r\n");

    String dtstamp = ZonedDateTime.now(ZoneId.of("UTC"))
        .format(ICAL_DATETIME_FORMATTER);
    vevent.append("DTSTAMP:").append(dtstamp).append("\r\n");

    String dtstart = convertToUtc(event.getStartDateTime(), timezone);
    vevent.append("DTSTART:").append(dtstart).append("\r\n");

    String dtend = convertToUtc(event.getEndDateTime(), timezone);
    vevent.append("DTEND:").append(dtend).append("\r\n");

    vevent.append("SUMMARY:").append(foldLine(escapeText(event.getSubject())))
        .append("\r\n");

    if (event.getDescription().isPresent()) {
      vevent.append("DESCRIPTION:").append(foldLine(escapeText(event.getDescription().get())))
          .append("\r\n");
    }

    if (event.getLocation().isPresent()) {
      vevent.append("LOCATION:").append(foldLine(escapeText(event.getLocation().get())))
          .append("\r\n");
    }

    if (event.isPrivate()) {
      vevent.append("CLASS:PRIVATE\r\n");
    } else {
      vevent.append("CLASS:PUBLIC\r\n");
    }

    if (event.getSeriesId().isPresent()) {
      vevent.append("X-SERIES-ID:").append(event.getSeriesId().get().toString()).append("\r\n");
    }

    vevent.append("END:VEVENT\r\n");

    return vevent.toString();
  }

  /**
   * Converts LocalDateTime to UTC format for iCal.
   *
   * @param dateTime the local date-time
   * @param timezone the timezone of the date-time
   * @return the UTC formatted string (yyyyMMddTHHmmssZ)
   */
  private static String convertToUtc(LocalDateTime dateTime, ZoneId timezone) {
    ZonedDateTime zoned = dateTime.atZone(timezone);
    ZonedDateTime utc = zoned.withZoneSameInstant(ZoneId.of("UTC"));
    return utc.format(ICAL_DATETIME_FORMATTER);
  }

  /**
   * Escapes special characters in text fields according to RFC 5545.
   * Escapes: backslash, semicolon, comma, newline
   *
   * @param text the text to escape
   * @return the escaped text
   */
  private static String escapeText(String text) {
    if (text == null) {
      return "";
    }

    return text
        .replace("\\", "\\\\")  
        .replace(";", "\\;")
        .replace(",", "\\,")
        .replace("\n", "\\n")
        .replace("\r", "");     
  }

  /**
   * Folds long lines at RFC 5545 specified width.
   * Continuation lines start with a space.
   *
   * @param text the text to fold
   * @return the folded text (without line breaks, as they're added by caller)
   */
  private static String foldLine(String text) {
    if (text.length() <= RFC5545_LINE_FOLD_WIDTH) {
      return text;
    }

    StringBuilder folded = new StringBuilder();
    int pos = 0;

    while (pos < text.length()) {
      if (pos > 0) {
        folded.append("\r\n ");
      }

      int end = Math.min(pos + RFC5545_LINE_FOLD_WIDTH, text.length());
      folded.append(text.substring(pos, end));
      pos = end;
    }

    return folded.toString();
  }
}
