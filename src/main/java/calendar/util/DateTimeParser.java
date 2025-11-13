package calendar.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Date/time parsing helpers per README formats.
 */
public final class DateTimeParser {
  private DateTimeParser() {
  }

  /**
   * Parses a date string in format yyyy-MM-dd.
   *
   * @param dateString the date string
   * @return parsed LocalDate
   */
  public static LocalDate parseDate(String dateString) {
    return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }

  /**
   * Parses a time string in format HH:mm.
   *
   * @param timeString the time string
   * @return parsed LocalTime
   */
  public static LocalTime parseTime(String timeString) {
    return LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"));
  }

  /**
   * Parses a datetime string in format yyyy-MM-ddTHH:mm.
   *
   * @param dateTime the datetime string
   * @return parsed LocalDateTime
   */
  public static LocalDateTime parseDateTime(String dateTime) {
    return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
  }
}
