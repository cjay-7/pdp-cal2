import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import calendar.model.CalendarModel;
import calendar.model.CalendarModelInterface;
import calendar.model.EditSpec;
import calendar.model.Event;
import calendar.model.EventInterface;
import calendar.model.EventSeries;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.Test;

/**
 * Example test showing how to create, query, and test events.
 */
public class CalendarModelTest {

  @Test
  public void testCreateSingleEvent() {
    
    CalendarModelInterface model = new CalendarModel();

    
    LocalDateTime start = LocalDateTime.of(2025, 5, 5, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 5, 5, 11, 0);

    EventInterface event =
        new Event("Team Meeting", start, end, "Discuss project progress", "Conference Room A",
            false, 
            UUID.randomUUID(), null 
        );

    
    boolean success = model.createEvent(event);
    assertTrue("Event should be created", success);

    
    List<EventInterface> eventsOnDate = model.getEventsOnDate(LocalDate.of(2025, 5, 5));
    assertEquals("Should have one event", 1, eventsOnDate.size());
    assertEquals("Event subject should match", "Team Meeting", eventsOnDate.get(0).getSubject());
  }

  @Test
  public void testCreateEventSeries() {
    CalendarModelInterface model = new CalendarModel();

    
    LocalDateTime start = LocalDateTime.of(2025, 5, 5, 14, 0); 
    LocalDateTime end = LocalDateTime.of(2025, 5, 5, 15, 0); 

    EventInterface template =
        new Event("Weekly Standup", start, end, "Team standup meeting", "Zoom", false,
            UUID.randomUUID(), null);

    
    UUID seriesId = UUID.randomUUID();
    Set<java.time.DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(java.time.DayOfWeek.MONDAY);
    weekdays.add(java.time.DayOfWeek.WEDNESDAY);

    EventSeries series = new EventSeries(seriesId, template, weekdays, null,
        
        6, 
        false 
    );

    
    boolean success = model.createEventSeries(series);
    assertTrue("Series should be created", success);

    
    List<EventInterface> allEvents = model.getEventsInRange(LocalDateTime.of(2025, 5, 1, 0, 0),
        LocalDateTime.of(2025, 6, 1, 0, 0));

    
    long count = allEvents.stream().filter(e -> e.getSubject().equals("Weekly Standup")).count();

    assertEquals("Should have 6 occurrences", 6, count);
  }

  @Test
  public void testQueryEventsOnDate() {
    CalendarModelInterface model = new CalendarModel();

    
    LocalDate testDate = LocalDate.of(2025, 5, 10);

    EventInterface event1 =
        new Event("Morning Meeting", LocalDateTime.of(testDate, java.time.LocalTime.of(9, 0)),
            LocalDateTime.of(testDate, java.time.LocalTime.of(10, 0)), null, null, false,
            UUID.randomUUID(), null);

    EventInterface event2 =
        new Event("Lunch Break", LocalDateTime.of(testDate, java.time.LocalTime.of(12, 0)),
            LocalDateTime.of(testDate, java.time.LocalTime.of(13, 0)), null, null, false,
            UUID.randomUUID(), null);

    model.createEvent(event1);
    model.createEvent(event2);

    
    List<EventInterface> events = model.getEventsOnDate(testDate);
    assertEquals("Should have 2 events", 2, events.size());

    
    assertTrue(events.get(0).getStartDateTime().isBefore(events.get(1).getStartDateTime()));
  }

  @Test
  public void testQueryEventsInRange() {
    CalendarModelInterface model = new CalendarModel();

    
    LocalDateTime day1 = LocalDateTime.of(2025, 5, 10, 10, 0);
    LocalDateTime day2 = LocalDateTime.of(2025, 5, 12, 10, 0);
    LocalDateTime day3 = LocalDateTime.of(2025, 5, 15, 10, 0);

    model.createEvent(
        new Event("Event 1", day1, day1.plusHours(1), null, null, false, UUID.randomUUID(), null));
    model.createEvent(
        new Event("Event 2", day2, day2.plusHours(1), null, null, false, UUID.randomUUID(), null));
    model.createEvent(
        new Event("Event 3", day3, day3.plusHours(1), null, null, false, UUID.randomUUID(), null));

    
    List<EventInterface> events = model.getEventsInRange(LocalDateTime.of(2025, 5, 11, 0, 0),
        LocalDateTime.of(2025, 5, 14, 23, 59));

    
    assertEquals("Should have 1 event in range", 1, events.size());
    assertEquals("Should be Event 2", "Event 2", events.get(0).getSubject());
  }

  @Test
  public void testIsBusy() {
    CalendarModelInterface model = new CalendarModel();

    LocalDateTime eventStart = LocalDateTime.of(2025, 5, 10, 10, 0);
    LocalDateTime eventEnd = LocalDateTime.of(2025, 5, 10, 11, 0);

    model.createEvent(
        new Event("Meeting", eventStart, eventEnd, null, null, false, UUID.randomUUID(), null));

    
    assertTrue("Should be busy at 10:30", model.isBusy(LocalDateTime.of(2025, 5, 10, 10, 30)));

    
    assertFalse("Should not be busy at 9:00", model.isBusy(LocalDateTime.of(2025, 5, 10, 9, 0)));

    
    assertFalse("Should not be busy at 12:00", model.isBusy(LocalDateTime.of(2025, 5, 10, 12, 0)));
  }

  @Test
  public void testEditEvent() {
    CalendarModelInterface model = new CalendarModel();

    
    EventInterface event = new Event("Old Subject", LocalDateTime.of(2025, 5, 10, 10, 0),
        LocalDateTime.of(2025, 5, 10, 11, 0), "Old description", "Old location", false,
        UUID.randomUUID(), null);

    model.createEvent(event);
    UUID eventId = event.getId();

    
    EditSpec spec = new EditSpec("New Subject", 
        null, 
        null, 
        "New description", 
        null, 
        null 
    );

    boolean success = model.editEvent(eventId, spec);
    assertTrue("Edit should succeed", success);

    
    EventInterface edited = model.findEventById(eventId);
    assertNotNull("Event should exist", edited);
    assertEquals("Subject should be updated", "New Subject", edited.getSubject());
    assertEquals("Description should be updated", "New description",
        edited.getDescription().orElse(""));
  }

  @Test
  public void testFindEventByProperties() {
    CalendarModelInterface model = new CalendarModel();

    EventInterface event = new Event("Search Test", LocalDateTime.of(2025, 5, 10, 10, 0),
        LocalDateTime.of(2025, 5, 10, 11, 0), null, null, false, UUID.randomUUID(), null);

    model.createEvent(event);

    
    EventInterface found =
        model.findEventByProperties("Search Test", LocalDateTime.of(2025, 5, 10, 10, 0),
            LocalDateTime.of(2025, 5, 10, 11, 0));

    assertNotNull("Event should be found", found);
    assertEquals("Should match", event.getId(), found.getId());
  }
}