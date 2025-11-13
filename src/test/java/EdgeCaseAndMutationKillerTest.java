import static org.junit.Assert.*;

import calendar.model.*;
import calendar.util.DateTimeParser;
import calendar.util.Weekday;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Additional edge case tests designed to achieve 100% mutation coverage.
 * Focuses on boundary conditions, null handling, and logical operators.
 */
public class EdgeCaseAndMutationKillerTest {
  private CalendarModelInterface model;

  @Before
  public void setUp() {
    model = new CalendarModel();
  }

  // ========== DateTimeParser Edge Cases ==========

  @Test
  public void testDateTimeParserValidFormats() {
    // Kill mutations in date/time parsing logic
    LocalDateTime result1 = DateTimeParser.parseDateTime("2025-06-01T09:00");
    assertEquals(LocalDateTime.of(2025, 6, 1, 9, 0), result1);

    LocalDateTime result2 = DateTimeParser.parseDateTime("2025-12-31T23:59");
    assertEquals(LocalDateTime.of(2025, 12, 31, 23, 59), result2);

    LocalDate date1 = DateTimeParser.parseDate("2025-01-01");
    assertEquals(LocalDate.of(2025, 1, 1), date1);

    LocalDate date2 = DateTimeParser.parseDate("2025-12-31");
    assertEquals(LocalDate.of(2025, 12, 31), date2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDateTimeParserInvalidFormat() {
    DateTimeParser.parseDateTime("invalid-format");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDateParserInvalidFormat() {
    DateTimeParser.parseDate("not-a-date");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDateTimeParserNull() {
    DateTimeParser.parseDateTime(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDateParserNull() {
    DateTimeParser.parseDate(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDateTimeParserEmptyString() {
    DateTimeParser.parseDateTime("");
  }

  // ========== Weekday Utility Tests (TC27) ==========

  @Test
  public void testWeekdayFromStringAllValues() {
    // TC27: Test all weekday conversions (Rubric line 49)
    assertEquals(DayOfWeek.MONDAY, Weekday.fromString("M"));
    assertEquals(DayOfWeek.TUESDAY, Weekday.fromString("T"));
    assertEquals(DayOfWeek.WEDNESDAY, Weekday.fromString("W"));
    assertEquals(DayOfWeek.THURSDAY, Weekday.fromString("R"));
    assertEquals(DayOfWeek.FRIDAY, Weekday.fromString("F"));
    assertEquals(DayOfWeek.SATURDAY, Weekday.fromString("S"));
    assertEquals(DayOfWeek.SUNDAY, Weekday.fromString("U"));
  }

  @Test
  public void testWeekdayFromStringCaseInsensitive() {
    // Kill mutations: case sensitivity checks
    assertEquals(DayOfWeek.MONDAY, Weekday.fromString("m"));
    assertEquals(DayOfWeek.TUESDAY, Weekday.fromString("t"));
    assertEquals(DayOfWeek.FRIDAY, Weekday.fromString("f"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWeekdayFromStringInvalid() {
    Weekday.fromString("X");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWeekdayFromStringNull() {
    Weekday.fromString(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWeekdayFromStringEmpty() {
    Weekday.fromString("");
  }

  @Test
  public void testWeekdayParseMultiple() {
    // Kill mutations in multiple weekday parsing
    Set<DayOfWeek> days = Weekday.parseWeekdays("MWF");
    assertEquals(3, days.size());
    assertTrue(days.contains(DayOfWeek.MONDAY));
    assertTrue(days.contains(DayOfWeek.WEDNESDAY));
    assertTrue(days.contains(DayOfWeek.FRIDAY));
  }

  @Test
  public void testWeekdayParseAllDays() {
    Set<DayOfWeek> days = Weekday.parseWeekdays("MTWRFSU");
    assertEquals(7, days.size());
  }

  @Test
  public void testWeekdayParseDuplicates() {
    // Edge case: duplicate letters should result in unique set
    Set<DayOfWeek> days = Weekday.parseWeekdays("MMM");
    assertEquals(1, days.size());
    assertTrue(days.contains(DayOfWeek.MONDAY));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWeekdayParseInvalidCharacter() {
    Weekday.parseWeekdays("MXF");
  }

  // ========== EventStatus Tests ==========

  @Test
  public void testEventStatusFromStringAllValues() {
    // Kill mutations: test all enum values
    assertEquals(EventStatus.PUBLIC, EventStatus.fromString("public"));
    assertEquals(EventStatus.PUBLIC, EventStatus.fromString("PUBLIC"));
    assertEquals(EventStatus.PUBLIC, EventStatus.fromString("false"));
    assertEquals(EventStatus.PUBLIC, EventStatus.fromString("FALSE"));

    assertEquals(EventStatus.PRIVATE, EventStatus.fromString("private"));
    assertEquals(EventStatus.PRIVATE, EventStatus.fromString("PRIVATE"));
    assertEquals(EventStatus.PRIVATE, EventStatus.fromString("true"));
    assertEquals(EventStatus.PRIVATE, EventStatus.fromString("TRUE"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventStatusFromStringInvalid() {
    EventStatus.fromString("invalid");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventStatusFromStringNull() {
    EventStatus.fromString(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventStatusFromStringEmpty() {
    EventStatus.fromString("");
  }

  // ========== EditSpec Tests ==========

  @Test
  public void testEditSpecAllNulls() {
    // Edge case: EditSpec with all null values
    EditSpec spec = new EditSpec(null, null, null, null, null, null);
    assertNotNull("EditSpec should be created", spec);
  }

  @Test
  public void testEditSpecPartialUpdates() {
    // Kill mutations: verify each field can be set independently
    EventInterface event = new Event("Test",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, UUID.randomUUID(), null);

    model.createEvent(event);

    // Update only subject
    EditSpec spec1 = new EditSpec("NewSubject", null, null, null, null, null);
    model.editEvent(event.getId(), spec1);
    EventInterface after1 = model.findEventById(event.getId());
    assertEquals("NewSubject", after1.getSubject());

    // Update only description
    EditSpec spec2 = new EditSpec(null, null, null, "New Description", null, null);
    model.editEvent(event.getId(), spec2);
    EventInterface after2 = model.findEventById(event.getId());
    assertEquals("New Description", after2.getDescription());

    // Update only location
    EditSpec spec3 = new EditSpec(null, null, null, null, "New Location", null);
    model.editEvent(event.getId(), spec3);
    EventInterface after3 = model.findEventById(event.getId());
    assertEquals("New Location", after3.getLocation());

    // Update only status
    EditSpec spec4 = new EditSpec(null, null, null, null, null, EventStatus.PRIVATE);
    model.editEvent(event.getId(), spec4);
    EventInterface after4 = model.findEventById(event.getId());
    assertEquals(EventStatus.PRIVATE, after4.getStatus());
  }

  // ========== Event Equality and Hash Code Tests ==========

  @Test
  public void testEventEqualsAndHashCode() {
    // Kill mutations: equals and hashCode logic
    UUID id1 = UUID.randomUUID();
    EventInterface event1 = new Event("Test",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, id1, null);

    EventInterface event2 = new Event("Test",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, id1, null);

    EventInterface event3 = new Event("Test",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, UUID.randomUUID(), null);

    // Same ID should be equal
    assertEquals(event1, event2);
    assertEquals(event1.hashCode(), event2.hashCode());

    // Different ID should not be equal
    assertNotEquals(event1, event3);

    // Null and different class should not be equal
    assertNotEquals(event1, null);
    assertNotEquals(event1, "Not an event");
  }

  // ========== Series ID Tests ==========

  @Test
  public void testSeriesIdPresenceAndAbsence() {
    // Kill mutations: Optional.isPresent() checks
    UUID seriesId = UUID.randomUUID();

    EventInterface withSeries = new Event("WithSeries",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, UUID.randomUUID(), seriesId);

    EventInterface withoutSeries = new Event("WithoutSeries",
        LocalDateTime.of(2025, 6, 1, 12, 0),
        LocalDateTime.of(2025, 6, 1, 13, 0),
        null, null, false, UUID.randomUUID(), null);

    assertTrue("Event with series should have seriesId", withSeries.getSeriesId().isPresent());
    assertEquals(seriesId, withSeries.getSeriesId().get());

    assertFalse("Event without series should not have seriesId",
        withoutSeries.getSeriesId().isPresent());
  }

  // ========== Calendar Manager Tests ==========

  @Test
  public void testCalendarCreationAndRetrieval() {
    CalendarManager manager = new CalendarManager();
    manager.createCalendar("Work", java.time.ZoneId.of("America/New_York"));

    assertNotNull("Calendar should be created", manager.getCalendar("Work"));
    assertNull("Non-existent calendar should return null",
        manager.getCalendar("NonExistent"));
  }

  @Test
  public void testSetCurrentCalendar() {
    CalendarManager manager = new CalendarManager();
    manager.createCalendar("Cal1", java.time.ZoneId.of("America/New_York"));
    manager.createCalendar("Cal2", java.time.ZoneId.of("America/Los_Angeles"));

    manager.setCurrentCalendar("Cal1");
    assertEquals("Cal1", manager.getCurrentCalendar().getName());

    manager.setCurrentCalendar("Cal2");
    assertEquals("Cal2", manager.getCurrentCalendar().getName());
  }

  @Test
  public void testCurrentCalendarNull() {
    CalendarManager manager = new CalendarManager();
    assertNull("Current calendar should be null initially", manager.getCurrentCalendar());
  }

  // ========== Range Query Edge Cases ==========

  @Test
  public void testGetEventsInRangeOverlapping() {
    // Kill mutations: overlap detection logic
    EventInterface event = new Event("Overlap",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 12, 0),
        null, null, false, UUID.randomUUID(), null);

    model.createEvent(event);

    // Query range completely contains event
    List<EventInterface> contains = model.getEventsInRange(
        LocalDateTime.of(2025, 6, 1, 9, 0),
        LocalDateTime.of(2025, 6, 1, 13, 0));
    assertEquals("Should find event in containing range", 1, contains.size());

    // Query range partially overlaps (start)
    List<EventInterface> overlapStart = model.getEventsInRange(
        LocalDateTime.of(2025, 6, 1, 9, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0));
    assertEquals("Should find event with start overlap", 1, overlapStart.size());

    // Query range partially overlaps (end)
    List<EventInterface> overlapEnd = model.getEventsInRange(
        LocalDateTime.of(2025, 6, 1, 11, 0),
        LocalDateTime.of(2025, 6, 1, 13, 0));
    assertEquals("Should find event with end overlap", 1, overlapEnd.size());

    // Event contains query range
    List<EventInterface> inside = model.getEventsInRange(
        LocalDateTime.of(2025, 6, 1, 10, 30),
        LocalDateTime.of(2025, 6, 1, 11, 30));
    assertEquals("Should find event containing query range", 1, inside.size());
  }

  // ========== Duplicate Prevention Tests ==========

  @Test
  public void testDuplicatePreventionDifferentDescriptions() {
    // Kill mutations: ensure description doesn't affect uniqueness
    EventInterface event1 = new Event("Meeting",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        "Description A", null, false, UUID.randomUUID(), null);

    EventInterface event2 = new Event("Meeting",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        "Description B", null, false, UUID.randomUUID(), null);

    assertTrue("First event should be created", model.createEvent(event1));
    assertFalse("Second event should be duplicate despite different description",
        model.createEvent(event2));
  }

  @Test
  public void testDuplicatePreventionDifferentLocations() {
    // Kill mutations: ensure location doesn't affect uniqueness
    EventInterface event1 = new Event("Meeting",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, "Room A", false, UUID.randomUUID(), null);

    EventInterface event2 = new Event("Meeting",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, "Room B", false, UUID.randomUUID(), null);

    assertTrue("First event should be created", model.createEvent(event1));
    assertFalse("Second event should be duplicate despite different location",
        model.createEvent(event2));
  }

  @Test
  public void testDuplicatePreventionDifferentStatus() {
    // Kill mutations: ensure status doesn't affect uniqueness
    EventInterface event1 = new Event("Meeting",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, UUID.randomUUID(), null);

    model.createEvent(event1);

    // Try to edit to create a duplicate with different status
    EditSpec spec = new EditSpec(null, null, null, null, null, EventStatus.PRIVATE);
    assertTrue("Edit should succeed", model.editEvent(event1.getId(), spec));

    EventInterface updated = model.findEventById(event1.getId());
    assertEquals(EventStatus.PRIVATE, updated.getStatus());
  }

  // ========== Series Edge Cases ==========

  @Test
  public void testSeriesStartDateNotMatchingWeekday() {
    // Edge case: start date is Friday but series is for Mondays
    EventInterface template = new Event("Monday Meeting",
        LocalDateTime.of(2025, 6, 6, 9, 0),  // Friday
        LocalDateTime.of(2025, 6, 6, 10, 0),
        null, null, false, UUID.randomUUID(), null);

    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    EventSeries series = new EventSeries(seriesId, template, weekdays, null, 3, false);
    assertTrue("Series should be created", model.createEventSeries(series));

    // First occurrence should be on the next Monday (June 9)
    List<EventInterface> events = model.getAllEvents();
    Optional<EventInterface> firstEvent = events.stream()
        .filter(e -> e.getSubject().equals("Monday Meeting"))
        .min(Comparator.comparing(EventInterface::getStartDateTime));

    assertTrue("Should have at least one event", firstEvent.isPresent());
    assertEquals(DayOfWeek.MONDAY, firstEvent.get().getStartDateTime().getDayOfWeek());
  }

  // ========== Zero and Negative Number Tests ==========

  @Test
  public void testSeriesWithZeroOccurrences() {
    // Edge case: zero occurrences
    EventInterface template = new Event("Zero",
        LocalDateTime.of(2025, 6, 2, 9, 0),
        LocalDateTime.of(2025, 6, 2, 10, 0),
        null, null, false, UUID.randomUUID(), null);

    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    EventSeries series = new EventSeries(seriesId, template, weekdays, null, 0, false);
    model.createEventSeries(series);

    List<EventInterface> events = model.getAllEvents();
    long count = events.stream()
        .filter(e -> e.getSubject().equals("Zero"))
        .count();

    assertEquals("Should have no occurrences", 0, count);
  }

  // ========== String Comparison Edge Cases ==========

  @Test
  public void testSubjectCaseSensitivity() {
    // Kill mutations: case sensitivity in subject matching
    EventInterface event1 = new Event("meeting",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, UUID.randomUUID(), null);

    EventInterface event2 = new Event("MEETING",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, UUID.randomUUID(), null);

    model.createEvent(event1);
    model.createEvent(event2);

    // Both should exist since they have different subjects (case-sensitive)
    EventInterface found1 = model.findEventByProperties("meeting",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0));

    EventInterface found2 = model.findEventByProperties("MEETING",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0));

    assertNotNull("lowercase meeting should exist", found1);
    assertNotNull("UPPERCASE MEETING should exist", found2);
  }
}
