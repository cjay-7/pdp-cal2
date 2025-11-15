import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import calendar.command.CreateAllDayEventCommand;
import calendar.command.CreateEventCommand;
import calendar.command.EditEventCommand;
import calendar.command.EditEventsCommand;
import calendar.command.EditSeriesCommand;
import calendar.command.ExitCommand;
import calendar.command.ExportCommand;
import calendar.command.NoOpCommand;
import calendar.command.PrintAllEventsCommand;
import calendar.command.PrintEventsOnCommand;
import calendar.command.PrintEventsRangeCommand;
import calendar.command.ShowStatusCommand;
import calendar.exceptions.InvalidCommandException;
import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.CalendarModel;
import calendar.model.CalendarModelInterface;
import calendar.model.Event;
import calendar.model.EventInterface;
import calendar.model.EventSeries;
import calendar.view.ConsoleView;
import calendar.view.ViewInterface;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Comprehensive tests for all command classes.
 */
public class CommandTests {
  private CalendarManager manager;
  private CalendarModelInterface model;  
  private ViewInterface view;
  private ByteArrayOutputStream outContent;

  /**
   * Initializes manager, model, view, and output capture for command tests.
   */
  @Before
  public void setUp() {
    
    manager = new CalendarManager();
    manager.createCalendar("TestCalendar", ZoneId.of("America/New_York"));
    manager.setCurrentCalendar("TestCalendar");

    
    model = manager.getCurrentCalendar().getModel();

    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
    view = new ConsoleView(System.out);
  }

  /**
   * Restores System.out after each test.
   */
  @After
  public void tearDown() {
    System.setOut(System.out);
  }

  

  /**
   * Verifies creating a standard timed event succeeds.
   * TC 3: This test verifies that a single event is created with the required parameters.
   */
  @Test
  public void testCreateEventCommandSuccess() throws IOException {
    CreateEventCommand cmd = new CreateEventCommand("Meeting",
        "2025-06-01T09:00", "2025-06-01T10:00");
    assertTrue(cmd.execute(manager, view));

    
    EventInterface createdEvent = model.findEventByProperties("Meeting",
        LocalDateTime.of(2025, 6, 1, 9, 0),
        LocalDateTime.of(2025, 6, 1, 10, 0));
    assertFalse("Event should have been created", createdEvent == null);
    assertTrue("Subject should be 'Meeting'", createdEvent.getSubject().equals("Meeting"));
    assertTrue("Start time should be 09:00",
        createdEvent.getStartDateTime().equals(LocalDateTime.of(2025, 6, 1, 9, 0)));
    assertTrue("End time should be 10:00",
        createdEvent.getEndDateTime().equals(LocalDateTime.of(2025, 6, 1, 10, 0)));
  }

  /**
   * Verifies duplicate event creation returns false.
   */
  @Test
  public void testCreateEventCommandDuplicate() throws IOException {
    CreateEventCommand cmd = new CreateEventCommand("Meeting",
        "2025-06-01T09:00", "2025-06-01T10:00");
    cmd.execute(manager, view);
    assertFalse(cmd.execute(manager, view)); 
  }

  

  @Test
  
  public void testCreateAllDayEventCommand() throws IOException {
    CreateAllDayEventCommand cmd = new CreateAllDayEventCommand("Holiday", "2025-06-01");
    assertTrue(cmd.execute(manager, view));

    
    EventInterface createdEvent = model.findEventByProperties("Holiday",
        LocalDateTime.of(2025, 6, 1, 8, 0),
        LocalDateTime.of(2025, 6, 1, 17, 0));
    assertFalse("Event should have been created", createdEvent == null);
    assertTrue("Event should be all-day", createdEvent.isAllDayEvent());
    assertTrue("Start time should be 08:00",
        createdEvent.getStartDateTime().toLocalTime().equals(java.time.LocalTime.of(8, 0)));
    assertTrue("End time should be 17:00",
        createdEvent.getEndDateTime().toLocalTime().equals(java.time.LocalTime.of(17, 0)));
  }

  

  @Test
  public void testPrintAllEventsCommandEmpty() throws IOException {
    PrintAllEventsCommand cmd = new PrintAllEventsCommand();
    assertTrue(cmd.execute(manager, view));
  }

  @Test
  public void testPrintAllEventsCommandWithEvents() throws IOException {
    model.createEvent(new Event("Event1",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, UUID.randomUUID(), null));
    PrintAllEventsCommand cmd = new PrintAllEventsCommand();
    assertTrue(cmd.execute(manager, view));
  }

  

  @Test
  public void testPrintEventsOnCommand() throws IOException {
    model.createEvent(new Event("Meeting",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, UUID.randomUUID(), null));
    PrintEventsOnCommand cmd = new PrintEventsOnCommand("2025-06-01");
    assertTrue(cmd.execute(manager, view));
  }

  

  @Test
  public void testPrintEventsRangeCommand() throws IOException {
    PrintEventsRangeCommand cmd = new PrintEventsRangeCommand(
        "2025-06-01T00:00", "2025-06-30T23:59");
    assertTrue(cmd.execute(manager, view));
  }

  

  @Test
  public void testShowStatusCommandBusy() throws IOException {
    model.createEvent(new Event("Meeting",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, UUID.randomUUID(), null));
    ShowStatusCommand cmd = new ShowStatusCommand("2025-06-01T10:30");
    assertTrue(cmd.execute(manager, view));
  }

  @Test
  public void testShowStatusCommandAvailable() throws IOException {
    ShowStatusCommand cmd = new ShowStatusCommand("2025-06-01T15:00");
    assertTrue(cmd.execute(manager, view));
  }

  

  @Test
  
  
  public void testEditEventCommandSuccess() throws IOException {
    EventInterface event = new Event("Old",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, UUID.randomUUID(), null);
    model.createEvent(event);

    EditEventCommand cmd = new EditEventCommand("subject", "Old",
        "2025-06-01T10:00", "2025-06-01T11:00", "New");
    assertTrue(cmd.execute(manager, view));

    
    EventInterface editedEvent = model.findEventByProperties("New",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0));
    assertFalse("Edited event should exist", editedEvent == null);
    assertTrue("Subject should be 'New'", editedEvent.getSubject().equals("New"));

    
    EventInterface oldEvent = model.findEventByProperties("Old",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0));
    assertTrue("Old event should not exist", oldEvent == null);
  }

  @Test
  public void testEditEventCommandNotFound() throws IOException {
    EditEventCommand cmd = new EditEventCommand("subject", "NonExistent",
        "2025-06-01T10:00", "2025-06-01T11:00", "New");
    assertFalse(cmd.execute(manager, view));
  }

  @Test
  public void testEditEventCommandAllProperties() throws IOException {
    EventInterface event = new Event("Test",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, UUID.randomUUID(), null);
    model.createEvent(event);

    EditEventCommand cmd1 = new EditEventCommand("start", "Test",
        "2025-06-01T10:00", "2025-06-01T11:00", "2025-06-01T09:30");
    assertTrue(cmd1.execute(manager, view));
    
    
    EditEventCommand cmd2 = new EditEventCommand("end", "Test",
        "2025-06-01T09:30", "2025-06-01T10:30", "2025-06-01T12:00");
    assertTrue(cmd2.execute(manager, view));

    EditEventCommand cmd3 = new EditEventCommand("description", "Test",
        "2025-06-01T09:30", "2025-06-01T12:00", "New description");
    assertTrue(cmd3.execute(manager, view));

    EditEventCommand cmd4 = new EditEventCommand("location", "Test",
        "2025-06-01T09:30", "2025-06-01T12:00", "Room 101");
    assertTrue(cmd4.execute(manager, view));

    EditEventCommand cmd5 = new EditEventCommand("status", "Test",
        "2025-06-01T09:30", "2025-06-01T12:00", "private");
    assertTrue(cmd5.execute(manager, view));
  }

  @Test(expected = IllegalArgumentException.class)
  
  
  public void testEditEventCommandInvalidProperty() throws IOException {
    EditEventCommand cmd = new EditEventCommand("invalid", "Test",
        "2025-06-01T10:00", "2025-06-01T11:00", "value");
    EventInterface event = new Event("Test",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, UUID.randomUUID(), null);
    model.createEvent(event);
    cmd.execute(manager, view); 
  }

  

  @Test
  
  
  
  public void testEditEventsCommand() throws IOException {
    EventInterface template = new Event("Standup",
        LocalDateTime.of(2025, 6, 2, 9, 0),
        LocalDateTime.of(2025, 6, 2, 9, 30),
        null, null, false, UUID.randomUUID(), null);
    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);
    EventSeries series = new EventSeries(seriesId, template, weekdays, null, 3, false);
    model.createEventSeries(series);

    EditEventsCommand cmd = new EditEventsCommand("location", "Standup",
        "2025-06-09T09:00", "Room A");
    assertTrue(cmd.execute(manager, view));

    
    java.util.List<EventInterface> events = model.getAllEvents();
    long eventsWithLocation = events.stream()
        .filter(e -> e.getSubject().equals("Standup"))
        .filter(e -> !e.getStartDateTime().isBefore(LocalDateTime.of(2025, 6, 9, 9, 0)))
        .filter(e -> e.getLocation().isPresent() && e.getLocation().get().equals("Room A"))
        .count();
    assertTrue("At least one event should have updated location", eventsWithLocation > 0);
  }

  

  @Test
  
  
  
  public void testEditSeriesCommand() throws IOException {
    EventInterface template = new Event("Standup",
        LocalDateTime.of(2025, 6, 2, 9, 0),
        LocalDateTime.of(2025, 6, 2, 9, 30),
        null, null, false, UUID.randomUUID(), null);
    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);
    EventSeries series = new EventSeries(seriesId, template, weekdays, null, 3, false);
    model.createEventSeries(series);

    EditSeriesCommand cmd = new EditSeriesCommand("subject", "Standup",
        "2025-06-02T09:00", "Daily Standup");
    assertTrue(cmd.execute(manager, view));

    
    java.util.List<EventInterface> events = model.getAllEvents();
    long standupCount = events.stream()
        .filter(e -> e.getSubject().equals("Standup"))
        .count();
    long dailyStandupCount = events.stream()
        .filter(e -> e.getSubject().equals("Daily Standup"))
        .count();
    assertTrue("No events should have old subject 'Standup'", standupCount == 0);
    assertTrue("All 3 events should have new subject 'Daily Standup'", dailyStandupCount == 3);
  }

  

  @Test
  public void testExportCommand() throws IOException {
    model.createEvent(new Event("Test Event",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, UUID.randomUUID(), null));

    Path tempFile = Files.createTempFile("test-export", ".csv");
    try {
      ExportCommand cmd = new ExportCommand(tempFile.toString());
      assertTrue(cmd.execute(manager, view));
      assertTrue(Files.exists(tempFile));
    } finally {
      Files.deleteIfExists(tempFile);
    }
  }

  

  @Test
  public void testExitCommand() throws IOException {
    ExitCommand cmd = new ExitCommand();
    assertFalse(cmd.execute(manager, view)); 
  }

  

  @Test
  public void testNoOpCommandEmpty() throws IOException {
    NoOpCommand cmd = new NoOpCommand();
    assertTrue(cmd.execute(manager, view));
  }

  @Test(expected = InvalidCommandException.class)
  public void testNoOpCommandWithInput() throws IOException {
    NoOpCommand cmd = new NoOpCommand("invalid command");
    cmd.execute(manager, view); 
  }
}

