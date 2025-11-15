import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import calendar.model.CalendarModel;
import calendar.model.CalendarModelInterface;
import calendar.model.EditSpec;
import calendar.model.Event;
import calendar.model.EventInterface;
import calendar.model.EventSeries;
import calendar.model.EventStatus;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

/**
 * Comprehensive tests for CalendarModel to achieve 100% coverage.
 */
public class CalendarModelCompleteTest {
  private CalendarModelInterface model;

  /**
   * Initializes a new CalendarModel before each test.
   */
  @Before
  public void setUp() {
    model = new CalendarModel();
  }

  

  @Test
  
  
  public void testCreateEventSuccess() {
    EventInterface event = createTestEvent("Meeting", 2025, 6, 1, 10, 0, 11, 0);
    assertTrue(model.createEvent(event));
  }

  @Test
  
  
  
  public void testCreateEventDuplicate() {
    EventInterface event = createTestEvent("Meeting", 2025, 6, 1, 10, 0, 11, 0);
    assertTrue(model.createEvent(event));
    assertFalse(model.createEvent(event));
  }

  @Test
  public void testCreateEventDifferentPropertiesButSameKey() {
    EventInterface event1 = new Event("Meeting",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        "Desc1", "Loc1", false, UUID.randomUUID(), null);
    EventInterface event2 = new Event("Meeting",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        "Desc2", "Loc2", true, UUID.randomUUID(), null);

    assertTrue(model.createEvent(event1));
    assertFalse("Should not create duplicate (same subject, start, end)",
        model.createEvent(event2));
  }

  @Test(expected = NullPointerException.class)
  public void testCreateEventNull() {
    model.createEvent(null);
  }

  

  @Test
  
  
  public void testSeriesEventsDoNotSpanMultipleDays() {
    EventInterface template = createTestEvent("Daily Meeting", 2025, 6, 2, 9, 0, 10, 0);
    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);
    weekdays.add(DayOfWeek.TUESDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);

    EventSeries series = new EventSeries(seriesId, template, weekdays, null, 10, false);
    assertTrue(model.createEventSeries(series));

    List<EventInterface> events = model.getAllEvents();
    for (EventInterface event : events) {
      if (event.getSubject().equals("Daily Meeting")) {
        
        LocalDate startDate = event.getStartDateTime().toLocalDate();
        LocalDate endDate = event.getEndDateTime().toLocalDate();
        assertTrue("Event should not span multiple days - starts on " + startDate
            + " and ends on " + endDate, startDate.equals(endDate));
      }
    }
  }

  @Test
  
  
  
  
  public void testCreateEventSeriesWithOccurrences() {
    EventInterface template = createTestEvent("Standup", 2025, 6, 2, 9, 0, 9, 30);
    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);

    EventSeries series = new EventSeries(seriesId, template, weekdays, null, 5, false);
    assertTrue(model.createEventSeries(series));

    List<EventInterface> events = model.getAllEvents();
    List<EventInterface> standupEvents = events.stream()
        .filter(e -> e.getSubject().equals("Standup"))
        .collect(java.util.stream.Collectors.toList());

    
    assertEquals("Should have 5 occurrences", 5, standupEvents.size());

    
    for (EventInterface event : standupEvents) {
      assertEquals("All events should start at 9:00", 9, event.getStartDateTime().getHour());
      assertEquals("All events should start at 0 minutes", 0, event.getStartDateTime().getMinute());
      assertEquals("All events should end at 9:30", 9, event.getEndDateTime().getHour());
      assertEquals("All events should end at 30 minutes", 30, event.getEndDateTime().getMinute());
    }

    
    for (EventInterface event : standupEvents) {
      DayOfWeek day = event.getStartDateTime().getDayOfWeek();
      assertTrue("Event should be on Monday or Wednesday",
          day == DayOfWeek.MONDAY || day == DayOfWeek.WEDNESDAY);
    }
  }

  @Test
  
  
  
  public void testCreateEventSeriesWithEndDate() {
    EventInterface template = createTestEvent("Weekly Sync", 2025, 6, 3, 14, 0, 15, 0);
    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.TUESDAY);

    LocalDate endDate = LocalDate.of(2025, 6, 30);
    EventSeries series = new EventSeries(seriesId, template, weekdays, endDate, null, true);
    assertTrue(model.createEventSeries(series));

    List<EventInterface> events = model.getAllEvents();
    long syncCount = events.stream()
        .filter(e -> e.getSubject().equals("Weekly Sync"))
        .count();
    assertTrue("Should have multiple occurrences", syncCount > 0);
  }

  @Test
  
  
  
  public void testCreateEventSeriesDuplicate() {
    EventInterface template = createTestEvent("Standup", 2025, 6, 2, 9, 0, 9, 30);
    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    EventSeries series = new EventSeries(seriesId, template, weekdays, null, 3, false);
    assertTrue(model.createEventSeries(series));

    
    EventSeries duplicate = new EventSeries(seriesId, template, weekdays, null, 3, false);
    assertFalse("Should not create duplicate series", model.createEventSeries(duplicate));
  }

  @Test(expected = NullPointerException.class)
  public void testCreateEventSeriesNull() {
    model.createEventSeries(null);
  }

  

  @Test
  public void testEditEventSubject() {
    EventInterface event = createTestEvent("Old", 2025, 6, 1, 10, 0, 11, 0);
    model.createEvent(event);

    EditSpec spec = new EditSpec("New", null, null, null, null, null);
    assertTrue(model.editEvent(event.getId(), spec));

    EventInterface edited = model.findEventById(event.getId());
    assertEquals("New", edited.getSubject());
  }

  @Test
  public void testEditEventStartTime() {
    EventInterface event = createTestEvent("Meeting", 2025, 6, 1, 10, 0, 11, 0);
    model.createEvent(event);

    LocalDateTime newStart = LocalDateTime.of(2025, 6, 1, 9, 30);
    EditSpec spec = new EditSpec(null, newStart, null, null, null, null);
    assertTrue(model.editEvent(event.getId(), spec));

    EventInterface edited = model.findEventById(event.getId());
    assertEquals(newStart, edited.getStartDateTime());
  }

  @Test
  public void testEditEventEndTime() {
    EventInterface event = createTestEvent("Meeting", 2025, 6, 1, 10, 0, 11, 0);
    model.createEvent(event);

    LocalDateTime newEnd = LocalDateTime.of(2025, 6, 1, 12, 0);
    EditSpec spec = new EditSpec(null, null, newEnd, null, null, null);
    assertTrue(model.editEvent(event.getId(), spec));

    EventInterface edited = model.findEventById(event.getId());
    assertEquals(newEnd, edited.getEndDateTime());
  }

  @Test
  public void testEditEventDescription() {
    EventInterface event = createTestEvent("Meeting", 2025, 6, 1, 10, 0, 11, 0);
    model.createEvent(event);

    EditSpec spec = new EditSpec(null, null, null, "New desc", null, null);
    assertTrue(model.editEvent(event.getId(), spec));

    EventInterface edited = model.findEventById(event.getId());
    assertEquals("New desc", edited.getDescription().orElse(""));
  }

  @Test
  public void testEditEventLocation() {
    EventInterface event = createTestEvent("Meeting", 2025, 6, 1, 10, 0, 11, 0);
    model.createEvent(event);

    EditSpec spec = new EditSpec(null, null, null, null, "Room 202", null);
    assertTrue(model.editEvent(event.getId(), spec));

    EventInterface edited = model.findEventById(event.getId());
    assertEquals("Room 202", edited.getLocation().orElse(""));
  }

  @Test
  public void testEditEventStatus() {
    EventInterface event = createTestEvent("Meeting", 2025, 6, 1, 10, 0, 11, 0);
    model.createEvent(event);

    EditSpec spec = new EditSpec(null, null, null, null, null, EventStatus.PRIVATE);
    assertTrue(model.editEvent(event.getId(), spec));

    EventInterface edited = model.findEventById(event.getId());
    assertTrue(edited.isPrivate());
  }

  @Test
  public void testEditEventCreatesDuplicate() {
    EventInterface event1 = createTestEvent("Meeting", 2025, 6, 1, 10, 0, 11, 0);
    EventInterface event2 = createTestEvent("Other", 2025, 6, 1, 14, 0, 15, 0);
    model.createEvent(event1);
    model.createEvent(event2);

    
    EditSpec spec = new EditSpec("Meeting",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, null);
    assertFalse("Should not create duplicate", model.editEvent(event2.getId(), spec));
  }

  @Test
  public void testEditEventNotFound() {
    UUID nonExistentId = UUID.randomUUID();
    EditSpec spec = new EditSpec("New", null, null, null, null, null);
    assertFalse(model.editEvent(nonExistentId, spec));
  }

  @Test(expected = NullPointerException.class)
  public void testEditEventNullId() {
    EditSpec spec = new EditSpec("New", null, null, null, null, null);
    model.editEvent(null, spec);
  }

  @Test(expected = NullPointerException.class)
  public void testEditEventNullSpec() {
    EventInterface event = createTestEvent("Meeting", 2025, 6, 1, 10, 0, 11, 0);
    model.createEvent(event);
    model.editEvent(event.getId(), null);
  }

  

  @Test
  public void testEditSeriesFrom() {
    EventInterface template = createTestEvent("Standup", 2025, 6, 2, 9, 0, 9, 30);
    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);

    EventSeries series = new EventSeries(seriesId, template, weekdays, null, 5, false);
    model.createEventSeries(series);

    LocalDate fromDate = LocalDate.of(2025, 6, 16);
    EditSpec spec = new EditSpec(null, null, null, null, "Room A", null);
    assertTrue(model.editSeriesFrom(seriesId, fromDate, spec));

    List<EventInterface> events = model.getEventsOnDate(fromDate);
    long standupCount = events.stream()
        .filter(e -> e.getSubject().equals("Standup")
            && e.getLocation().isPresent()
            && e.getLocation().get().equals("Room A"))
        .count();
    assertTrue("Should have updated events", standupCount > 0);
  }

  @Test
  public void testEditSeriesFromNoSeries() {
    UUID nonExistentSeriesId = UUID.randomUUID();
    EditSpec spec = new EditSpec(null, null, null, null, "Room A", null);
    assertFalse(model.editSeriesFrom(nonExistentSeriesId, LocalDate.now(), spec));
  }

  @Test
  public void testEditEntireSeries() {
    EventInterface template = createTestEvent("Standup", 2025, 6, 2, 9, 0, 9, 30);
    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    EventSeries series = new EventSeries(seriesId, template, weekdays, null, 4, false);
    model.createEventSeries(series);

    EditSpec spec = new EditSpec("Daily Standup", null, null, null, null, null);
    assertTrue(model.editEntireSeries(seriesId, spec));

    List<EventInterface> events = model.getAllEvents();
    long standupCount = events.stream()
        .filter(e -> e.getSubject().equals("Daily Standup"))
        .count();
    assertEquals("All events should be updated", 4, standupCount);
  }

  @Test
  public void testEditEntireSeriesNoSeries() {
    UUID nonExistentSeriesId = UUID.randomUUID();
    EditSpec spec = new EditSpec("New", null, null, null, null, null);
    assertFalse(model.editEntireSeries(nonExistentSeriesId, spec));
  }

  @Test
  
  
  
  public void testEditSeriesFromWithStartTimeChange() {
    EventInterface template = createTestEvent("Standup", 2025, 6, 2, 9, 0, 9, 30);
    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    EventSeries series = new EventSeries(seriesId, template, weekdays, null, 3, false);
    model.createEventSeries(series);

    LocalDate fromDate = LocalDate.of(2025, 6, 9);
    LocalDateTime newStart = LocalDateTime.of(2025, 6, 9, 8, 30);
    EditSpec spec = new EditSpec(null, newStart, null, null, null, null);
    assertTrue(model.editSeriesFrom(seriesId, fromDate, spec));

    
    List<EventInterface> events = model.getAllEvents();
    for (EventInterface event : events) {
      if (event.getSubject().equals("Standup")
          && !event.getStartDateTime().toLocalDate().isBefore(fromDate)) {
        
        assertFalse("Event should not have series ID after start time edit",
            event.getSeriesId().isPresent());
      }
    }
  }

  

  @Test
  
  
  
  public void testGetEventsOnDate() {
    LocalDate date = LocalDate.of(2025, 6, 10);
    EventInterface event1 = new Event("Morning",
        LocalDateTime.of(date, java.time.LocalTime.of(9, 0)),
        LocalDateTime.of(date, java.time.LocalTime.of(10, 0)),
        null, null, false, UUID.randomUUID(), null);
    EventInterface event2 = new Event("Afternoon",
        LocalDateTime.of(date, java.time.LocalTime.of(14, 0)),
        LocalDateTime.of(date, java.time.LocalTime.of(15, 0)),
        null, null, false, UUID.randomUUID(), null);

    model.createEvent(event1);
    model.createEvent(event2);

    List<EventInterface> events = model.getEventsOnDate(date);
    assertEquals(2, events.size());
    assertEquals("Morning", events.get(0).getSubject());
    assertEquals("Afternoon", events.get(1).getSubject());
  }

  @Test
  public void testGetEventsOnDateMultiDayEvent() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 9, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 6, 11, 15, 0);
    EventInterface event = new Event("Multi-day", start, end,
        null, null, false, UUID.randomUUID(), null);
    model.createEvent(event);

    List<EventInterface> events = model.getEventsOnDate(LocalDate.of(2025, 6, 10));
    assertEquals("Event spanning multiple days should appear on middle day", 1, events.size());
  }

  @Test
  public void testGetEventsOnDateEmpty() {
    List<EventInterface> events = model.getEventsOnDate(LocalDate.of(2025, 6, 10));
    assertTrue(events.isEmpty());
  }

  @Test
  
  
  
  public void testGetEventsInRange() {
    LocalDateTime rangeStart = LocalDateTime.of(2025, 6, 10, 0, 0);
    LocalDateTime rangeEnd = LocalDateTime.of(2025, 6, 12, 23, 59);

    EventInterface event1 = createTestEvent("Event1", 2025, 6, 9, 10, 0, 11, 0);
    EventInterface event2 = createTestEvent("Event2", 2025, 6, 11, 10, 0, 11, 0);
    EventInterface event3 = createTestEvent("Event3", 2025, 6, 13, 10, 0, 11, 0);

    model.createEvent(event1);
    model.createEvent(event2);
    model.createEvent(event3);

    List<EventInterface> events = model.getEventsInRange(rangeStart, rangeEnd);
    assertEquals("Should only return Event2", 1, events.size());
    assertEquals("Event2", events.get(0).getSubject());
  }

  @Test
  
  
  
  public void testGetEventsInRangeOverlapping() {
    EventInterface event = new Event("Long Event",
        LocalDateTime.of(2025, 6, 9, 10, 0),
        LocalDateTime.of(2025, 6, 11, 15, 0),
        null, null, false, UUID.randomUUID(), null);
    model.createEvent(event);

    List<EventInterface> events = model.getEventsInRange(
        LocalDateTime.of(2025, 6, 10, 0, 0),
        LocalDateTime.of(2025, 6, 10, 23, 59));
    assertEquals("Should include overlapping event", 1, events.size());
  }

  @Test
  public void testGetAllEvents() {
    model.createEvent(createTestEvent("Event1", 2025, 6, 1, 10, 0, 11, 0));
    model.createEvent(createTestEvent("Event2", 2025, 6, 2, 14, 0, 15, 0));
    model.createEvent(createTestEvent("Event3", 2025, 6, 1, 9, 0, 9, 30));

    List<EventInterface> events = model.getAllEvents();
    assertEquals(3, events.size());
    
    assertTrue(events.get(0).getStartDateTime().isBefore(events.get(1).getStartDateTime()));
  }

  @Test
  
  
  
  public void testIsBusy() {
    EventInterface event = createTestEvent("Meeting", 2025, 6, 10, 10, 0, 11, 0);
    model.createEvent(event);

    assertTrue("Should be busy during event",
        model.isBusy(LocalDateTime.of(2025, 6, 10, 10, 30)));
    assertFalse("Should not be busy before event",
        model.isBusy(LocalDateTime.of(2025, 6, 10, 9, 0)));
    assertFalse("Should not be busy after event",
        model.isBusy(LocalDateTime.of(2025, 6, 10, 12, 0)));
    assertTrue("Should be busy at start time",
        model.isBusy(LocalDateTime.of(2025, 6, 10, 10, 0)));
  }

  @Test
  public void testIsBusyMultipleEvents() {
    model.createEvent(createTestEvent("Event1", 2025, 6, 10, 10, 0, 11, 0));
    model.createEvent(createTestEvent("Event2", 2025, 6, 10, 14, 0, 15, 0));

    assertTrue(model.isBusy(LocalDateTime.of(2025, 6, 10, 10, 30)));
    assertFalse(model.isBusy(LocalDateTime.of(2025, 6, 10, 12, 0)));
    assertTrue(model.isBusy(LocalDateTime.of(2025, 6, 10, 14, 30)));
  }

  

  @Test
  public void testFindEventById() {
    EventInterface event = createTestEvent("Meeting", 2025, 6, 1, 10, 0, 11, 0);
    model.createEvent(event);
    UUID eventId = event.getId();

    EventInterface found = model.findEventById(eventId);
    assertNotNull(found);
    assertEquals(eventId, found.getId());
    assertEquals("Meeting", found.getSubject());
  }

  @Test
  public void testFindEventByIdNotFound() {
    UUID nonExistentId = UUID.randomUUID();
    assertNull(model.findEventById(nonExistentId));
  }

  @Test(expected = NullPointerException.class)
  public void testFindEventByIdNull() {
    model.findEventById(null);
  }

  @Test
  public void testFindEventByProperties() {
    EventInterface event = createTestEvent("Search Test", 2025, 6, 1, 10, 0, 11, 0);
    model.createEvent(event);

    EventInterface found = model.findEventByProperties("Search Test",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0));

    assertNotNull(found);
    assertEquals(event.getId(), found.getId());
  }

  @Test
  public void testFindEventByPropertiesNotFound() {
    assertNull(model.findEventByProperties("Non-existent",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0)));
  }

  @Test(expected = NullPointerException.class)
  public void testFindEventByPropertiesNullSubject() {
    model.findEventByProperties(null,
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0));
  }

  

  @Test
  
  
  
  
  public void testExportToCsv() throws IOException {
    model.createEvent(createTestEvent("Event1", 2025, 6, 1, 10, 0, 11, 0));
    model.createEvent(createTestEvent("Event2", 2025, 6, 2, 14, 0, 15, 0));

    Path tempFile = Files.createTempFile("test-calendar", ".csv");
    try {
      model.exportToCsv(tempFile);

      assertTrue(Files.exists(tempFile));
      String content = Files.readString(tempFile);
      assertTrue(content.contains("Subject"));
      assertTrue(content.contains("Event1"));
      assertTrue(content.contains("Event2"));
    } finally {
      Files.deleteIfExists(tempFile);
    }
  }

  @Test(expected = NullPointerException.class)
  public void testExportToCsvNullPath() throws IOException {
    model.exportToCsv(null);
  }

  @Test(expected = NullPointerException.class)
  public void testGetEventsOnDateNull() {
    model.getEventsOnDate(null);
  }

  @Test(expected = NullPointerException.class)
  public void testGetEventsInRangeNullStart() {
    model.getEventsInRange(null, LocalDateTime.now());
  }

  @Test(expected = NullPointerException.class)
  public void testGetEventsInRangeNullEnd() {
    model.getEventsInRange(LocalDateTime.now(), null);
  }

  @Test(expected = NullPointerException.class)
  public void testIsBusyNull() {
    model.isBusy(null);
  }

  
  private EventInterface createTestEvent(String subject, int year, int month, int day,
                                         int startHour, int startMin, int endHour, int endMin) {
    LocalDateTime start = LocalDateTime.of(year, month, day, startHour, startMin);
    LocalDateTime end = LocalDateTime.of(year, month, day, endHour, endMin);
    return new Event(subject, start, end, null, null, false, UUID.randomUUID(), null);
  }
}

