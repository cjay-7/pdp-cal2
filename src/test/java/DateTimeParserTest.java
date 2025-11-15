import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import calendar.util.DateTimeParser;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.Test;

/**
 * Tests for DateTimeParser utility class.
 */
public class DateTimeParserTest {

  @Test
  public void testParseDate() {
    LocalDate date = DateTimeParser.parseDate("2025-06-01");
    assertNotNull(date);
    assertEquals(2025, date.getYear());
    assertEquals(6, date.getMonthValue());
    assertEquals(1, date.getDayOfMonth());
  }

  @Test
  public void testParseDateVariousDates() {
    assertEquals(LocalDate.of(2025, 1, 1), DateTimeParser.parseDate("2025-01-01"));
    assertEquals(LocalDate.of(2025, 12, 31), DateTimeParser.parseDate("2025-12-31"));
    assertEquals(LocalDate.of(2024, 2, 29), DateTimeParser.parseDate("2024-02-29")); 
  }

  @Test
  public void testParseTime() {
    LocalTime time = DateTimeParser.parseTime("09:30");
    assertNotNull(time);
    assertEquals(9, time.getHour());
    assertEquals(30, time.getMinute());
  }

  @Test
  public void testParseTimeVariousTimes() {
    assertEquals(LocalTime.of(0, 0), DateTimeParser.parseTime("00:00"));
    assertEquals(LocalTime.of(23, 59), DateTimeParser.parseTime("23:59"));
    assertEquals(LocalTime.of(12, 30), DateTimeParser.parseTime("12:30"));
  }

  @Test
  public void testParseDateTime() {
    LocalDateTime dateTime = DateTimeParser.parseDateTime("2025-06-01T09:30");
    assertNotNull(dateTime);
    assertEquals(2025, dateTime.getYear());
    assertEquals(6, dateTime.getMonthValue());
    assertEquals(1, dateTime.getDayOfMonth());
    assertEquals(9, dateTime.getHour());
    assertEquals(30, dateTime.getMinute());
  }

  @Test
  public void testParseDateTimeVarious() {
    assertEquals(LocalDateTime.of(2025, 6, 1, 0, 0),
        DateTimeParser.parseDateTime("2025-06-01T00:00"));
    assertEquals(LocalDateTime.of(2025, 12, 31, 23, 59),
        DateTimeParser.parseDateTime("2025-12-31T23:59"));
  }
}

