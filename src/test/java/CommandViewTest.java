import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import calendar.command.CreateAllDayEventCommand;
import calendar.command.CreateAllDayEventSeriesCommand;
import calendar.command.CreateEventCommand;
import calendar.command.CreateEventSeriesFromToCommand;
import calendar.command.ExportCommand;
import calendar.command.PrintAllEventsCommand;
import calendar.command.PrintEventsOnCommand;
import calendar.command.PrintEventsRangeCommand;
import calendar.command.ShowStatusCommand;
import calendar.model.Calendar;
import calendar.model.CalendarManager;
import java.io.IOException;
import java.time.ZoneId;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for command execution with view interaction verification.
 */
public class CommandViewTest {
  private CalendarManager manager;
  private TestView view;

  /**
   * Sets up the test environment with a new CalendarManager and TestView.
   */
  @Before
  public void setUp() {
    manager = new CalendarManager();
    view = new TestView();
  }

  @Test
  public void testCreateAllDayEventCommandNoCalendar() throws IOException {
    CreateAllDayEventCommand cmd = new CreateAllDayEventCommand("Meeting", "2025-06-01");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false when no calendar selected", result);
    assertEquals("Should display error", 1, view.getErrorCount());
    assertTrue("Should show 'No calendar selected' error",
        view.hasError("No calendar selected"));
  }

  @Test
  public void testCreateAllDayEventCommandSuccess() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    CreateAllDayEventCommand cmd = new CreateAllDayEventCommand("Meeting", "2025-06-01");
    boolean result = cmd.execute(manager, view);

    assertTrue("Should return true on success", result);
    assertEquals("Should display success message", 1, view.getMessageCount());
    assertTrue("Should show created message", view.hasMessage("Created all-day event"));
  }

  @Test
  public void testCreateAllDayEventCommandDuplicate() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    CreateAllDayEventCommand cmd1 = new CreateAllDayEventCommand("Meeting", "2025-06-01");
    cmd1.execute(manager, view);
    view.clear();

    CreateAllDayEventCommand cmd2 = new CreateAllDayEventCommand("Meeting", "2025-06-01");
    boolean result = cmd2.execute(manager, view);

    assertFalse("Should return false for duplicate", result);
    assertEquals("Should display error", 1, view.getErrorCount());
    assertTrue("Should show duplicate error", view.hasError("Duplicate event"));
  }

  @Test
  public void testCreateAllDayEventCommandInvalidDate() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    CreateAllDayEventCommand cmd = new CreateAllDayEventCommand("Meeting", "invalid-date");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false for invalid date", result);
    assertEquals("Should display error", 1, view.getErrorCount());
    assertTrue("Should show error message", view.hasError("Failed to create"));
  }

  @Test
  public void testCreateEventCommandNoCalendar() throws IOException {
    CreateEventCommand cmd = new CreateEventCommand("Meeting",
        "2025-06-01T09:00", "2025-06-01T10:00");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false when no calendar", result);
    assertTrue("Should show error", view.hasError("No calendar selected"));
  }

  @Test
  public void testCreateEventCommandSuccess() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    CreateEventCommand cmd = new CreateEventCommand("Meeting",
        "2025-06-01T09:00", "2025-06-01T10:00");
    boolean result = cmd.execute(manager, view);

    assertTrue("Should return true on success", result);
    assertTrue("Should show success message", view.hasMessage("Created event"));
  }

  @Test
  public void testCreateEventCommandDuplicate() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    CreateEventCommand cmd1 = new CreateEventCommand("Meeting",
        "2025-06-01T09:00", "2025-06-01T10:00");
    cmd1.execute(manager, view);
    view.clear();

    CreateEventCommand cmd2 = new CreateEventCommand("Meeting",
        "2025-06-01T09:00", "2025-06-01T10:00");
    boolean result = cmd2.execute(manager, view);

    assertFalse("Should return false for duplicate", result);
    assertTrue("Should show duplicate error", view.hasError("Duplicate event"));
  }

  @Test
  public void testCreateEventCommandInvalidDateTime() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    CreateEventCommand cmd = new CreateEventCommand("Meeting", "bad-date", "bad-time");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false for invalid datetime", result);
    assertTrue("Should show error", view.hasError("Failed to create event"));
  }

  @Test
  public void testCreateAllDayEventSeriesCommandNoCalendar() throws IOException {
    CreateAllDayEventSeriesCommand cmd = new CreateAllDayEventSeriesCommand(
        "Holiday", "2025-06-01", "M", 5, null, false);
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("No calendar selected"));
  }

  @Test
  public void testCreateAllDayEventSeriesCommandSuccess() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    CreateAllDayEventSeriesCommand cmd = new CreateAllDayEventSeriesCommand(
        "Holiday", "2025-06-01", "M", 5, null, false);
    boolean result = cmd.execute(manager, view);

    assertTrue("Should return true", result);
    assertTrue("Should show success", view.hasMessage("Created"));
  }

  @Test
  public void testCreateAllDayEventSeriesCommandInvalid() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    CreateAllDayEventSeriesCommand cmd = new CreateAllDayEventSeriesCommand(
        "Holiday", "bad-date", "X", 5, null, false);
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("Failed"));
  }

  @Test
  public void testCreateEventSeriesFromToCommandNoCalendar() throws IOException {
    CreateEventSeriesFromToCommand cmd = new CreateEventSeriesFromToCommand(
        "Standup", "2025-06-01T09:00", "2025-06-01T09:30", "MWF", 5, null, false);
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("No calendar selected"));
  }

  @Test
  public void testCreateEventSeriesFromToCommandSuccess() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    CreateEventSeriesFromToCommand cmd = new CreateEventSeriesFromToCommand(
        "Standup", "2025-06-01T09:00", "2025-06-01T09:30", "MWF", 5, null, false);
    boolean result = cmd.execute(manager, view);

    assertTrue("Should return true", result);
    assertTrue("Should show success", view.hasMessage("Created"));
  }

  @Test
  public void testCreateEventSeriesFromToCommandInvalid() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    CreateEventSeriesFromToCommand cmd = new CreateEventSeriesFromToCommand(
        "Standup", "bad", "bad", "X", 5, null, false);
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("Failed"));
  }

  @Test
  public void testPrintAllEventsCommandNoCalendar() throws IOException {
    PrintAllEventsCommand cmd = new PrintAllEventsCommand();
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("No calendar selected"));
  }

  @Test
  public void testPrintAllEventsCommandSuccess() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    PrintAllEventsCommand cmd = new PrintAllEventsCommand();
    boolean result = cmd.execute(manager, view);

    assertTrue("Should return true", result);
    assertEquals("Should display events", 1, view.getDisplayedEvents().size());
  }

  @Test
  public void testPrintEventsOnCommandNoCalendar() throws IOException {
    PrintEventsOnCommand cmd = new PrintEventsOnCommand("2025-06-01");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("No calendar selected"));
  }

  @Test
  public void testPrintEventsOnCommandSuccess() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    PrintEventsOnCommand cmd = new PrintEventsOnCommand("2025-06-01");
    boolean result = cmd.execute(manager, view);

    assertTrue("Should return true", result);
    assertEquals("Should display events", 1, view.getDisplayedEvents().size());
  }

  @Test
  public void testPrintEventsOnCommandInvalid() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    PrintEventsOnCommand cmd = new PrintEventsOnCommand("bad-date");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("Failed"));
  }

  @Test
  public void testPrintEventsRangeCommandNoCalendar() throws IOException {
    PrintEventsRangeCommand cmd = new PrintEventsRangeCommand(
        "2025-06-01T00:00", "2025-06-30T23:59");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("No calendar selected"));
  }

  @Test
  public void testPrintEventsRangeCommandSuccess() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    PrintEventsRangeCommand cmd = new PrintEventsRangeCommand(
        "2025-06-01T00:00", "2025-06-30T23:59");
    boolean result = cmd.execute(manager, view);

    assertTrue("Should return true", result);
    assertEquals("Should display events", 1, view.getDisplayedEvents().size());
  }

  @Test
  public void testPrintEventsRangeCommandInvalid() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    PrintEventsRangeCommand cmd = new PrintEventsRangeCommand("bad", "bad");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("Failed"));
  }

  @Test
  public void testShowStatusCommandNoCalendar() throws IOException {
    ShowStatusCommand cmd = new ShowStatusCommand("2025-06-01T10:00");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("No calendar selected"));
  }

  @Test
  public void testShowStatusCommandAvailable() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    ShowStatusCommand cmd = new ShowStatusCommand("2025-06-01T10:00");
    boolean result = cmd.execute(manager, view);

    assertTrue("Should return true", result);
    assertTrue("Should show available", view.hasMessage("available"));
  }

  @Test
  public void testShowStatusCommandBusy() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    
    CreateEventCommand createCmd = new CreateEventCommand("Meeting",
        "2025-06-01T10:00", "2025-06-01T11:00");
    createCmd.execute(manager, view);
    view.clear();

    ShowStatusCommand cmd = new ShowStatusCommand("2025-06-01T10:30");
    boolean result = cmd.execute(manager, view);

    assertTrue("Should return true", result);
    assertTrue("Should show busy", view.hasMessage("busy"));
  }

  @Test
  public void testShowStatusCommandInvalid() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    ShowStatusCommand cmd = new ShowStatusCommand("bad-datetime");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("Failed"));
  }

  @Test
  public void testExportCommandNoCalendar() throws IOException {
    ExportCommand cmd = new ExportCommand("output.csv");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasMessage("No calendar"));
  }

  @Test
  public void testExportCommandUnsupportedFormat() throws IOException {
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");

    ExportCommand cmd = new ExportCommand("output.xml");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasMessage("Unsupported"));
  }
}
