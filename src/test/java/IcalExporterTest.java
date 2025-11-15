import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import calendar.model.Event;
import calendar.model.EventInterface;
import calendar.utils.IcalExporter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;

/**
 * Test class for IcalExporter.
 */
public class IcalExporterTest {

  @Test
  public void testConstructorThrowsException() {
    try {
      Constructor<IcalExporter> constructor = IcalExporter.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      assertThrows(InvocationTargetException.class, constructor::newInstance);
    } catch (NoSuchMethodException e) {
      fail("Constructor not found");
    }
  }

  @Test
  public void testToIcalEmptyList() {
    List<EventInterface> events = new ArrayList<>();
    String result = IcalExporter.toIcal(events, "TestCal", ZoneId.of("America/New_York"));

    assertTrue(result.contains("BEGIN:VCALENDAR"));
    assertTrue(result.contains("VERSION:2.0"));
    assertTrue(result.contains("PRODID:-//Calendar//TestCal//EN"));
    assertTrue(result.contains("CALSCALE:GREGORIAN"));
    assertTrue(result.contains("METHOD:PUBLISH"));
    assertTrue(result.contains("END:VCALENDAR"));
  }

  @Test
  public void testToIcalSingleEvent() {
    List<EventInterface> events = new ArrayList<>();
    Event event = new Event(
        "Meeting",
        LocalDateTime.of(2025, 6, 1, 9, 0),
        LocalDateTime.of(2025, 6, 1, 10, 0),
        "Team sync",
        "Room A",
        false,
        UUID.randomUUID(),
        null
    );
    events.add(event);

    String result = IcalExporter.toIcal(events, "WorkCal", ZoneId.of("UTC"));

    assertTrue(result.contains("BEGIN:VEVENT"));
    assertTrue(result.contains("END:VEVENT"));
    assertTrue(result.contains("SUMMARY:Meeting"));
    assertTrue(result.contains("DESCRIPTION:Team sync"));
    assertTrue(result.contains("LOCATION:Room A"));
    assertTrue(result.contains("CLASS:PUBLIC"));
    assertTrue(result.contains("UID:" + event.getId().toString() + "@calendar.app"));
  }

  @Test
  public void testToIcalPrivateEvent() {
    List<EventInterface> events = new ArrayList<>();
    Event event = new Event(
        "Private Meeting",
        LocalDateTime.of(2025, 6, 1, 14, 0),
        LocalDateTime.of(2025, 6, 1, 15, 0),
        null,
        null,
        true,
        UUID.randomUUID(),
        null
    );
    events.add(event);

    String result = IcalExporter.toIcal(events, "PersonalCal", ZoneId.of("America/Los_Angeles"));

    assertTrue(result.contains("SUMMARY:Private Meeting"));
    assertTrue(result.contains("CLASS:PRIVATE"));
    assertFalse(result.contains("DESCRIPTION:"));
    assertFalse(result.contains("LOCATION:"));
  }

  @Test
  public void testToIcalEventWithSeries() {
    List<EventInterface> events = new ArrayList<>();
    UUID seriesId = UUID.randomUUID();
    Event event = new Event(
        "Recurring",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null,
        null,
        false,
        UUID.randomUUID(),
        seriesId
    );
    events.add(event);

    String result = IcalExporter.toIcal(events, "TestCal", ZoneId.of("Europe/London"));

    assertTrue(result.contains("X-SERIES-ID:" + seriesId.toString()));
  }

  @Test
  public void testToIcalMultipleEvents() {
    List<EventInterface> events = new ArrayList<>();
    events.add(new Event(
        "Event1",
        LocalDateTime.of(2025, 6, 1, 9, 0),
        LocalDateTime.of(2025, 6, 1, 10, 0),
        null,
        null,
        false,
        UUID.randomUUID(),
        null
    ));
    events.add(new Event(
        "Event2",
        LocalDateTime.of(2025, 6, 2, 14, 0),
        LocalDateTime.of(2025, 6, 2, 15, 0),
        null,
        null,
        false,
        UUID.randomUUID(),
        null
    ));

    String result = IcalExporter.toIcal(events, "MultiCal", ZoneId.of("Asia/Tokyo"));

    int beginCount = result.split("BEGIN:VEVENT", -1).length - 1;
    assertEquals(2, beginCount);
    assertTrue(result.contains("SUMMARY:Event1"));
    assertTrue(result.contains("SUMMARY:Event2"));
  }

  @Test
  public void testToIcalSpecialCharactersInText() {
    List<EventInterface> events = new ArrayList<>();
    Event event = new Event(
        "Test;with,special\\chars",
        LocalDateTime.of(2025, 6, 1, 9, 0),
        LocalDateTime.of(2025, 6, 1, 10, 0),
        "Line1\nLine2",
        "Room;A,B\\C",
        false,
        UUID.randomUUID(),
        null
    );
    events.add(event);

    String result = IcalExporter.toIcal(events, "Test\\Cal;Name,Here", ZoneId.of("UTC"));

    assertTrue(result.contains("Test\\;with\\,special\\\\chars"));
    assertTrue(result.contains("Line1\\nLine2"));
    assertTrue(result.contains("Room\\;A\\,B\\\\C"));
    assertTrue(result.contains("Test\\\\Cal\\;Name\\,Here"));
  }

  @Test
  public void testToIcalLongTextLineFolding() {
    List<EventInterface> events = new ArrayList<>();
    String longSummary = "This is a very long summary that exceeds 75 characters "
        + "and should be folded";
    Event event = new Event(
        longSummary,
        LocalDateTime.of(2025, 6, 1, 9, 0),
        LocalDateTime.of(2025, 6, 1, 10, 0),
        "This is another very long description that also exceeds 75 characters and needs folding",
        null,
        false,
        UUID.randomUUID(),
        null
    );
    events.add(event);

    String result = IcalExporter.toIcal(events, "TestCal", ZoneId.of("UTC"));

    assertTrue(result.contains(longSummary));
    assertTrue(result.contains("SUMMARY:"));
    assertTrue(result.contains("DESCRIPTION:"));
  }

  @Test
  public void testToIcalTimezoneConversion() {
    List<EventInterface> events = new ArrayList<>();
    Event event = new Event(
        "TZ Test",
        LocalDateTime.of(2025, 6, 1, 12, 0),
        LocalDateTime.of(2025, 6, 1, 13, 0),
        null,
        null,
        false,
        UUID.randomUUID(),
        null
    );
    events.add(event);

    String result = IcalExporter.toIcal(events, "TestCal", ZoneId.of("America/New_York"));

    
    assertTrue(result.contains("DTSTART:"));
    assertTrue(result.contains("DTEND:"));
    assertTrue(result.matches("(?s).*DTSTART:\\d{8}T\\d{6}Z.*"));
    assertTrue(result.matches("(?s).*DTEND:\\d{8}T\\d{6}Z.*"));
  }

  @Test
  public void testToIcalCalendarNameEscaping() {
    List<EventInterface> events = new ArrayList<>();
    String result = IcalExporter.toIcal(events, "Cal;Name,With\\Special", ZoneId.of("UTC"));

    assertTrue(result.contains("PRODID:-//Calendar//Cal\\;Name\\,With\\\\Special//EN"));
  }

  @Test
  public void testToIcalNullDescription() {
    List<EventInterface> events = new ArrayList<>();
    Event event = new Event(
        "Test",
        LocalDateTime.of(2025, 6, 1, 9, 0),
        LocalDateTime.of(2025, 6, 1, 10, 0),
        null,
        "Location",
        false,
        UUID.randomUUID(),
        null
    );
    events.add(event);

    String result = IcalExporter.toIcal(events, "TestCal", ZoneId.of("UTC"));

    assertFalse(result.contains("DESCRIPTION:"));
    assertTrue(result.contains("LOCATION:Location"));
  }

  @Test
  public void testToIcalNullLocation() {
    List<EventInterface> events = new ArrayList<>();
    Event event = new Event(
        "Test",
        LocalDateTime.of(2025, 6, 1, 9, 0),
        LocalDateTime.of(2025, 6, 1, 10, 0),
        "Description",
        null,
        false,
        UUID.randomUUID(),
        null
    );
    events.add(event);

    String result = IcalExporter.toIcal(events, "TestCal", ZoneId.of("UTC"));

    assertTrue(result.contains("DESCRIPTION:Description"));
    assertFalse(result.contains("LOCATION:"));
  }
}
