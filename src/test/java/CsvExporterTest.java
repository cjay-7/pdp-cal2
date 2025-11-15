import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import calendar.model.Event;
import calendar.model.EventInterface;
import calendar.util.CsvExporter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.Test;

/**
 * Tests for CsvExporter utility.
 */
public class CsvExporterTest {

  /**
   * Ensures header and basic row fields are present and formatted.
   */
  @Test
  public void testHeaderAndRow() {
    EventInterface e = new Event("Subject",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        "Desc", "Loc", false, UUID.randomUUID(), null);
    String csv = CsvExporter.toCsv(List.of(e));
    assertTrue(csv.startsWith("Subject,Start Date,Start Time,End Date,End Time,"));
    assertTrue(csv.contains("2025/06/01"));
    assertTrue(csv.contains("10:00 AM"));
    assertTrue(csv.contains("11:00 AM"));
    assertTrue(csv.contains(",False,"));
  }

  /**
   * Ensures escaping of commas and quotes.
   */
  @Test
  public void testEscaping() {
    EventInterface e = new Event("A, \"B\"",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        "Hello, \"World\"", "Room, 101", false, UUID.randomUUID(), null);
    String csv = CsvExporter.toCsv(List.of(e));
    
    assertTrue(csv.contains("\"A, \"\"B\"\"\""));
    assertTrue(csv.contains("\"Hello, \"\"World\"\"\""));
    assertTrue(csv.contains("\"Room, 101\""));
  }

  /**
   * Ensures all-day events are marked True when 8:00-17:00 same day.
   */
  @Test
  public void testAllDayEventFlag() {
    EventInterface allDay = new Event("Holiday",
        LocalDateTime.of(2025, 6, 1, 8, 0),
        LocalDateTime.of(2025, 6, 1, 17, 0),
        null, null, false, UUID.randomUUID(), null);
    String csv = CsvExporter.toCsv(List.of(allDay));
    assertTrue(csv.contains(",True,"));

    EventInterface notAllDay = new Event("Meeting",
        LocalDateTime.of(2025, 6, 1, 8, 0),
        LocalDateTime.of(2025, 6, 1, 16, 59),
        null, null, false, UUID.randomUUID(), null);
    String csv2 = CsvExporter.toCsv(List.of(notAllDay));
    assertFalse(csv2.contains(",True,"));
  }
}
