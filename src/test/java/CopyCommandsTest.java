import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import calendar.command.CopyEventCommand;
import calendar.command.CopyEventsOnDayCommand;
import calendar.command.CopyEventsRangeCommand;
import calendar.model.CalendarManager;
import calendar.model.Event;
import calendar.view.ConsoleView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for copy commands: CopyEvent, CopyEventsOnDay, CopyEventsRange.
 */
public class CopyCommandsTest {

  private CalendarManager manager;
  private ByteArrayOutputStream outputStream;
  private ConsoleView view;

  /**
   * Sets up the test environment before each test.
   */
  @Before
  public void setUp() {
    manager = new CalendarManager();
    outputStream = new ByteArrayOutputStream();
    view = new ConsoleView(new PrintStream(outputStream));

    
    manager.createCalendar("SourceCal", ZoneId.of("America/New_York"));
    manager.createCalendar("TargetCal", ZoneId.of("America/Los_Angeles"));
    manager.setCurrentCalendar("SourceCal");
  }

  @Test
  public void testCopyEventSuccess() throws IOException {
    Event event = new Event(
        "Meeting",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        "Team sync",
        "Room A",
        false,
        UUID.randomUUID(),
        null
    );
    manager.getCurrentCalendar().getModel().createEvent(event);

    CopyEventCommand command = new CopyEventCommand(
        "Meeting",
        "2025-06-01T10:00",
        "TargetCal",
        "2025-06-02T14:00"
    );
    boolean result = command.execute(manager, view);

    assertTrue(result);
    String output = outputStream.toString();
    assertTrue(output.contains("copied"));
    assertTrue(output.contains("TargetCal"));
  }

  @Test
  public void testCopyEventNoCurrentCalendar() throws IOException {
    
    CalendarManager freshManager = new CalendarManager();
    freshManager.createCalendar("TargetCal", ZoneId.of("America/Los_Angeles"));

    CopyEventCommand command = new CopyEventCommand(
        "Meeting",
        "2025-06-01T10:00",
        "TargetCal",
        "2025-06-02T14:00"
    );
    boolean result = command.execute(freshManager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("No calendar is currently in use"));
  }

  @Test
  public void testCopyEventTargetNotFound() throws IOException {
    CopyEventCommand command = new CopyEventCommand(
        "Meeting",
        "2025-06-01T10:00",
        "NonExistent",
        "2025-06-02T14:00"
    );
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("not found"));
  }

  @Test
  public void testCopyEventInvalidSourceDateTime() throws IOException {
    CopyEventCommand command = new CopyEventCommand(
        "Meeting",
        "invalid-date",
        "TargetCal",
        "2025-06-02T14:00"
    );
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("Invalid") && output.contains("date"));
  }

  @Test
  public void testCopyEventInvalidTargetDateTime() throws IOException {
    Event event = new Event(
        "Meeting",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null,
        null,
        false,
        UUID.randomUUID(),
        null
    );
    manager.getCurrentCalendar().getModel().createEvent(event);

    CopyEventCommand command = new CopyEventCommand(
        "Meeting",
        "2025-06-01T10:00",
        "TargetCal",
        "invalid-date"
    );
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("Invalid") && output.contains("date"));
  }

  @Test
  public void testCopyEventNotFound() throws IOException {
    CopyEventCommand command = new CopyEventCommand(
        "NonExistent",
        "2025-06-01T10:00",
        "TargetCal",
        "2025-06-02T14:00"
    );
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("not found"));
  }

  @Test
  public void testCopyEventsOnDaySuccess() throws IOException {
    Event event1 = new Event(
        "Event1",
        LocalDateTime.of(2025, 6, 1, 9, 0),
        LocalDateTime.of(2025, 6, 1, 10, 0),
        null,
        null,
        false,
        UUID.randomUUID(),
        null
    );
    Event event2 = new Event(
        "Event2",
        LocalDateTime.of(2025, 6, 1, 14, 0),
        LocalDateTime.of(2025, 6, 1, 15, 0),
        null,
        null,
        false,
        UUID.randomUUID(),
        null
    );
    manager.getCurrentCalendar().getModel().createEvent(event1);
    manager.getCurrentCalendar().getModel().createEvent(event2);

    CopyEventsOnDayCommand command = new CopyEventsOnDayCommand(
        "2025-06-01",
        "TargetCal",
        "2025-06-05"
    );
    boolean result = command.execute(manager, view);

    assertTrue(result);
    String output = outputStream.toString();
    assertTrue(output.contains("Copied 2 event(s)"));
  }

  @Test
  public void testCopyEventsOnDayNoCurrentCalendar() throws IOException {
    
    CalendarManager freshManager = new CalendarManager();
    freshManager.createCalendar("TargetCal", ZoneId.of("America/Los_Angeles"));

    CopyEventsOnDayCommand command = new CopyEventsOnDayCommand(
        "2025-06-01",
        "TargetCal",
        "2025-06-05"
    );
    boolean result = command.execute(freshManager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("No calendar is currently in use"));
  }

  @Test
  public void testCopyEventsOnDayTargetNotFound() throws IOException {
    CopyEventsOnDayCommand command = new CopyEventsOnDayCommand(
        "2025-06-01",
        "NonExistent",
        "2025-06-05"
    );
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("not found"));
  }

  @Test
  public void testCopyEventsOnDayInvalidDate() throws IOException {
    CopyEventsOnDayCommand command = new CopyEventsOnDayCommand(
        "invalid-date",
        "TargetCal",
        "2025-06-05"
    );
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("Invalid date format"));
  }

  @Test
  public void testCopyEventsOnDayNoEvents() throws IOException {
    CopyEventsOnDayCommand command = new CopyEventsOnDayCommand(
        "2025-06-01",
        "TargetCal",
        "2025-06-05"
    );
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("No events found"));
  }

  @Test
  public void testCopyEventsRangeSuccess() throws IOException {
    Event event1 = new Event(
        "Event1",
        LocalDateTime.of(2025, 6, 1, 9, 0),
        LocalDateTime.of(2025, 6, 1, 10, 0),
        null,
        null,
        false,
        UUID.randomUUID(),
        null
    );
    Event event2 = new Event(
        "Event2",
        LocalDateTime.of(2025, 6, 3, 14, 0),
        LocalDateTime.of(2025, 6, 3, 15, 0),
        null,
        null,
        false,
        UUID.randomUUID(),
        null
    );
    Event event3 = new Event(
        "Event3",
        LocalDateTime.of(2025, 6, 10, 14, 0),
        LocalDateTime.of(2025, 6, 10, 15, 0),
        null,
        null,
        false,
        UUID.randomUUID(),
        null
    );
    manager.getCurrentCalendar().getModel().createEvent(event1);
    manager.getCurrentCalendar().getModel().createEvent(event2);
    manager.getCurrentCalendar().getModel().createEvent(event3);

    CopyEventsRangeCommand command = new CopyEventsRangeCommand(
        "2025-06-01",
        "2025-06-05",
        "TargetCal",
        "2025-06-10"
    );
    boolean result = command.execute(manager, view);

    assertTrue(result);
    String output = outputStream.toString();
    assertTrue(output.contains("Copied 2 event(s)"));
  }

  @Test
  public void testCopyEventsRangeNoCurrentCalendar() throws IOException {
    
    CalendarManager freshManager = new CalendarManager();
    freshManager.createCalendar("TargetCal", ZoneId.of("America/Los_Angeles"));

    CopyEventsRangeCommand command = new CopyEventsRangeCommand(
        "2025-06-01",
        "2025-06-05",
        "TargetCal",
        "2025-06-10"
    );
    boolean result = command.execute(freshManager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("No calendar is currently in use"));
  }

  @Test
  public void testCopyEventsRangeTargetNotFound() throws IOException {
    CopyEventsRangeCommand command = new CopyEventsRangeCommand(
        "2025-06-01",
        "2025-06-05",
        "NonExistent",
        "2025-06-10"
    );
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("not found"));
  }

  @Test
  public void testCopyEventsRangeInvalidDate() throws IOException {
    CopyEventsRangeCommand command = new CopyEventsRangeCommand(
        "invalid-date",
        "2025-06-05",
        "TargetCal",
        "2025-06-10"
    );
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("Invalid date format"));
  }

  @Test
  public void testCopyEventsRangeEndBeforeStart() throws IOException {
    CopyEventsRangeCommand command = new CopyEventsRangeCommand(
        "2025-06-05",
        "2025-06-01",
        "TargetCal",
        "2025-06-10"
    );
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("End date must be after"));
  }

  @Test
  public void testCopyEventsRangeNoEvents() throws IOException {
    CopyEventsRangeCommand command = new CopyEventsRangeCommand(
        "2025-06-01",
        "2025-06-05",
        "TargetCal",
        "2025-06-10"
    );
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("No events found"));
  }

  @Test
  public void testCopyEventsRangeWithSeriesId() throws IOException {
    UUID seriesId = UUID.randomUUID();
    Event event1 = new Event(
        "Series1",
        LocalDateTime.of(2025, 6, 1, 9, 0),
        LocalDateTime.of(2025, 6, 1, 10, 0),
        null,
        null,
        false,
        UUID.randomUUID(),
        seriesId
    );
    Event event2 = new Event(
        "Series2",
        LocalDateTime.of(2025, 6, 2, 9, 0),
        LocalDateTime.of(2025, 6, 2, 10, 0),
        null,
        null,
        false,
        UUID.randomUUID(),
        seriesId
    );
    manager.getCurrentCalendar().getModel().createEvent(event1);
    manager.getCurrentCalendar().getModel().createEvent(event2);

    CopyEventsRangeCommand command = new CopyEventsRangeCommand(
        "2025-06-01",
        "2025-06-05",
        "TargetCal",
        "2025-06-10"
    );
    boolean result = command.execute(manager, view);

    assertTrue(result);
    String output = outputStream.toString();
    assertTrue(output.contains("Copied 2 event(s)"));
  }
}
