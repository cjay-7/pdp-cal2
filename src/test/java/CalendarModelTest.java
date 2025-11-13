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
    // Create the model
    CalendarModelInterface model = new CalendarModel();

    // Create a single event
    LocalDateTime start = LocalDateTime.of(2025, 5, 5, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 5, 5, 11, 0);

    EventInterface event =
        new Event("Team Meeting", start, end, "Discuss project progress", "Conference Room A",
            false, // public
            UUID.randomUUID(), null // not part of a series
        );

    // Add event to calendar
    boolean success = model.createEvent(event);
    assertTrue("Event should be created", success);

    // Query events on that date
    List<EventInterface> eventsOnDate = model.getEventsOnDate(LocalDate.of(2025, 5, 5));
    assertEquals("Should have one event", 1, eventsOnDate.size());
    assertEquals("Event subject should match", "Team Meeting", eventsOnDate.get(0).getSubject());
  }

  @Test
  public void testCreateEventSeries() {
    CalendarModelInterface model = new CalendarModel();

    // Create template event for series
    LocalDateTime start = LocalDateTime.of(2025, 5, 5, 14, 0); // May 5, 2pm
    LocalDateTime end = LocalDateTime.of(2025, 5, 5, 15, 0); // May 5, 3pm

    EventInterface template =
        new Event("Weekly Standup", start, end, "Team standup meeting", "Zoom", false,
            UUID.randomUUID(), null);

    // Create series - repeats on Mondays and Wednesdays for 6 times
    UUID seriesId = UUID.randomUUID();
    Set<java.time.DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(java.time.DayOfWeek.MONDAY);
    weekdays.add(java.time.DayOfWeek.WEDNESDAY);

    EventSeries series = new EventSeries(seriesId, template, weekdays, null,
        // endDate (null because using occurrences)
        6, // 6 occurrences
        false // not using endDate, using occurrences
    );

    // Create the series
    boolean success = model.createEventSeries(series);
    assertTrue("Series should be created", success);

    // Query - should have 6 events (on Mondays and Wednesdays)
    List<EventInterface> allEvents = model.getEventsInRange(LocalDateTime.of(2025, 5, 1, 0, 0),
        LocalDateTime.of(2025, 6, 1, 0, 0));

    // Count events with this subject
    long count = allEvents.stream().filter(e -> e.getSubject().equals("Weekly Standup")).count();

    assertEquals("Should have 6 occurrences", 6, count);
  }

  @Test
  public void testQueryEventsOnDate() {
    CalendarModelInterface model = new CalendarModel();

    // Create multiple events on same day
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

    // Query events on that date
    List<EventInterface> events = model.getEventsOnDate(testDate);
    assertEquals("Should have 2 events", 2, events.size());

    // Events should be sorted by start time
    assertTrue(events.get(0).getStartDateTime().isBefore(events.get(1).getStartDateTime()));
  }

  @Test
  public void testQueryEventsInRange() {
    CalendarModelInterface model = new CalendarModel();

    // Create events on different days
    LocalDateTime day1 = LocalDateTime.of(2025, 5, 10, 10, 0);
    LocalDateTime day2 = LocalDateTime.of(2025, 5, 12, 10, 0);
    LocalDateTime day3 = LocalDateTime.of(2025, 5, 15, 10, 0);

    model.createEvent(
        new Event("Event 1", day1, day1.plusHours(1), null, null, false, UUID.randomUUID(), null));
    model.createEvent(
        new Event("Event 2", day2, day2.plusHours(1), null, null, false, UUID.randomUUID(), null));
    model.createEvent(
        new Event("Event 3", day3, day3.plusHours(1), null, null, false, UUID.randomUUID(), null));

    // Query range: May 11 to May 14
    List<EventInterface> events = model.getEventsInRange(LocalDateTime.of(2025, 5, 11, 0, 0),
        LocalDateTime.of(2025, 5, 14, 23, 59));

    // Should only get Event 2 (May 12)
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

    // Check if busy during the event
    assertTrue("Should be busy at 10:30", model.isBusy(LocalDateTime.of(2025, 5, 10, 10, 30)));

    // Check if busy before event
    assertFalse("Should not be busy at 9:00", model.isBusy(LocalDateTime.of(2025, 5, 10, 9, 0)));

    // Check if busy after event
    assertFalse("Should not be busy at 12:00", model.isBusy(LocalDateTime.of(2025, 5, 10, 12, 0)));
  }

  @Test
  public void testEditEvent() {
    CalendarModelInterface model = new CalendarModel();

    // Create an event
    EventInterface event = new Event("Old Subject", LocalDateTime.of(2025, 5, 10, 10, 0),
        LocalDateTime.of(2025, 5, 10, 11, 0), "Old description", "Old location", false,
        UUID.randomUUID(), null);

    model.createEvent(event);
    UUID eventId = event.getId();

    // Edit the event - change subject and description
    EditSpec spec = new EditSpec("New Subject", // new subject
        null, // keep start time
        null, // keep end time
        "New description", // new description
        null, // keep location
        null // keep status
    );

    boolean success = model.editEvent(eventId, spec);
    assertTrue("Edit should succeed", success);

    // Verify changes
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

    // Find by properties
    EventInterface found =
        model.findEventByProperties("Search Test", LocalDateTime.of(2025, 5, 10, 10, 0),
            LocalDateTime.of(2025, 5, 10, 11, 0));

    assertNotNull("Event should be found", found);
    assertEquals("Should match", event.getId(), found.getId());
  }
}