import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import calendar.command.CopyEventCommand;
import calendar.command.CopyEventsOnDayCommand;
import calendar.command.CopyEventsRangeCommand;
import calendar.command.CreateAllDayEventCommand;
import calendar.command.CreateEventCommand;
import calendar.command.CreateEventSeriesFromToCommand;
import calendar.command.EditEventCommand;
import calendar.command.EditEventsCommand;
import calendar.command.EditSeriesCommand;
import calendar.model.CalendarManager;
import java.io.IOException;
import java.time.ZoneId;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for edit and copy commands with view interaction verification.
 */
public class EditCopyCommandViewTest {
  private CalendarManager manager;
  private TestView view;

  /**
   * Sets up the test environment with a calendar and event for testing.
   *
   * @throws IOException if there is an error during setup
   */
  @Before
  public void setUp() throws IOException {
    manager = new CalendarManager();
    view = new TestView();

    
    manager.createCalendar("Work", ZoneId.systemDefault());
    manager.setCurrentCalendar("Work");
  }

  @Test
  public void testEditEventCommandNoCalendar() throws IOException {
    CalendarManager emptyManager = new CalendarManager();
    EditEventCommand cmd = new EditEventCommand("subject", "Meeting",
        "2025-06-01T09:00", "2025-06-01T10:00", "NewMeeting");
    boolean result = cmd.execute(emptyManager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("No calendar selected"));
  }

  @Test
  public void testEditEventCommandEventNotFound() throws IOException {
    EditEventCommand cmd = new EditEventCommand("subject", "NonExistent",
        "2025-06-01T09:00", "2025-06-01T10:00", "New");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("not found"));
  }

  @Test
  public void testEditEventCommandSuccess() throws IOException {
    
    CreateEventCommand create = new CreateEventCommand("Meeting",
        "2025-06-01T09:00", "2025-06-01T10:00");
    create.execute(manager, view);
    view.clear();

    EditEventCommand cmd = new EditEventCommand("subject", "Meeting",
        "2025-06-01T09:00", "2025-06-01T10:00", "UpdatedMeeting");
    boolean result = cmd.execute(manager, view);

    assertTrue("Should return true", result);
    assertTrue("Should show success", view.hasMessage("edited"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventCommandInvalidProperty() throws IOException {
    CreateEventCommand create = new CreateEventCommand("Meeting",
        "2025-06-01T09:00", "2025-06-01T10:00");
    create.execute(manager, view);
    view.clear();

    EditEventCommand cmd = new EditEventCommand("invalidprop", "Meeting",
        "2025-06-01T09:00", "2025-06-01T10:00", "value");
    cmd.execute(manager, view); 
  }

  @Test
  public void testEditEventCommandInvalidDateTime() throws IOException {
    EditEventCommand cmd = new EditEventCommand("subject", "Meeting",
        "bad", "bad", "New");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("Failed"));
  }

  @Test
  public void testEditEventsCommandNoCalendar() throws IOException {
    CalendarManager emptyManager = new CalendarManager();
    EditEventsCommand cmd = new EditEventsCommand("subject", "Meeting",
        "2025-06-01T09:00", "NewMeeting");
    boolean result = cmd.execute(emptyManager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("No calendar selected"));
  }

  @Test
  public void testEditEventsCommandNotFound() throws IOException {
    EditEventsCommand cmd = new EditEventsCommand("subject", "NonExistent",
        "2025-06-01T09:00", "New");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("not found"));
  }

  @Test
  public void testEditEventsCommandSuccess() throws IOException {
    CreateEventCommand create = new CreateEventCommand("Meeting",
        "2025-06-01T09:00", "2025-06-01T10:00");
    create.execute(manager, view);
    view.clear();

    EditEventsCommand cmd = new EditEventsCommand("subject", "Meeting",
        "2025-06-01T09:00", "UpdatedMeeting");
    boolean result = cmd.execute(manager, view);

    assertTrue("Should return true", result);
    assertTrue("Should show success", view.hasMessage("edited"));
  }

  @Test
  public void testEditSeriesCommandNoCalendar() throws IOException {
    CalendarManager emptyManager = new CalendarManager();
    EditSeriesCommand cmd = new EditSeriesCommand("subject", "Series",
        "2025-06-01T09:00", "NewSeries");
    boolean result = cmd.execute(emptyManager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("No calendar selected"));
  }

  @Test
  public void testEditSeriesCommandNotFound() throws IOException {
    EditSeriesCommand cmd = new EditSeriesCommand("subject", "NonExistent",
        "2025-06-01T09:00", "New");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasError("not found"));
  }

  @Test
  public void testEditSeriesCommandSuccess() throws IOException {
    
    CreateEventSeriesFromToCommand create = new CreateEventSeriesFromToCommand(
        "Standup", "2025-06-02T09:00", "2025-06-02T09:30", "MWF", 5, null, false);
    create.execute(manager, view);
    view.clear();

    EditSeriesCommand cmd = new EditSeriesCommand("subject", "Standup",
        "2025-06-02T09:00", "DailyStandup");
    boolean result = cmd.execute(manager, view);

    assertTrue("Should return true", result);
    assertTrue("Should show success", view.hasMessage("edited"));
  }

  @Test
  public void testCopyEventCommandNoCalendar() throws IOException {
    CalendarManager emptyManager = new CalendarManager();
    CopyEventCommand cmd = new CopyEventCommand("Meeting",
        "2025-06-01T09:00", "Work", "2025-06-02T09:00");
    boolean result = cmd.execute(emptyManager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasMessage("No calendar"));
  }

  @Test
  public void testCopyEventCommandNotFound() throws IOException {
    CopyEventCommand cmd = new CopyEventCommand("NonExistent",
        "2025-06-01T09:00", "Work", "2025-06-02T09:00");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasMessage("not found"));
  }

  @Test
  public void testCopyEventCommandSuccess() throws IOException {
    CreateEventCommand create = new CreateEventCommand("Meeting",
        "2025-06-01T09:00", "2025-06-01T10:00");
    create.execute(manager, view);
    view.clear();

    CopyEventCommand cmd = new CopyEventCommand("Meeting",
        "2025-06-01T09:00", "Work", "2025-06-02T09:00");
    boolean result = cmd.execute(manager, view);

    assertTrue("Should return true", result);
    assertTrue("Should show success", view.hasMessage("copied"));
  }

  @Test
  public void testCopyEventCommandInvalidDateTime() throws IOException {
    CopyEventCommand cmd = new CopyEventCommand("Meeting",
        "bad", "Work", "bad");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasMessage("Invalid"));
  }

  @Test
  public void testCopyEventsOnDayCommandNoCalendar() throws IOException {
    CalendarManager emptyManager = new CalendarManager();
    CopyEventsOnDayCommand cmd = new CopyEventsOnDayCommand("2025-06-01", "Work", "2025-06-02");
    boolean result = cmd.execute(emptyManager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasMessage("No calendar"));
  }

  @Test
  public void testCopyEventsOnDayCommandNoEvents() throws IOException {
    CopyEventsOnDayCommand cmd = new CopyEventsOnDayCommand("2025-06-01", "Work", "2025-06-02");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasMessage("No events found"));
  }

  @Test
  public void testCopyEventsOnDayCommandSuccess() throws IOException {
    CreateAllDayEventCommand create = new CreateAllDayEventCommand("Holiday", "2025-06-01");
    create.execute(manager, view);
    view.clear();

    CopyEventsOnDayCommand cmd = new CopyEventsOnDayCommand("2025-06-01", "Work", "2025-06-02");
    boolean result = cmd.execute(manager, view);

    assertTrue("Should return true", result);
    assertTrue("Should show success", view.hasMessage("Copied"));
  }

  @Test
  public void testCopyEventsOnDayCommandInvalidDate() throws IOException {
    CopyEventsOnDayCommand cmd = new CopyEventsOnDayCommand("bad", "Work", "bad");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasMessage("Invalid"));
  }

  @Test
  public void testCopyEventsRangeCommandNoCalendar() throws IOException {
    CalendarManager emptyManager = new CalendarManager();
    CopyEventsRangeCommand cmd = new CopyEventsRangeCommand(
        "2025-06-01", "2025-06-01", "Work", "2025-06-02");
    boolean result = cmd.execute(emptyManager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasMessage("No calendar"));
  }

  @Test
  public void testCopyEventsRangeCommandNoEvents() throws IOException {
    CopyEventsRangeCommand cmd = new CopyEventsRangeCommand(
        "2025-06-01", "2025-06-01", "Work", "2025-06-02");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasMessage("No events found"));
  }

  @Test
  public void testCopyEventsRangeCommandSuccess() throws IOException {
    CreateEventCommand create = new CreateEventCommand("Meeting",
        "2025-06-01T10:00", "2025-06-01T11:00");
    create.execute(manager, view);
    view.clear();

    CopyEventsRangeCommand cmd = new CopyEventsRangeCommand(
        "2025-06-01", "2025-06-01", "Work", "2025-06-02");
    boolean result = cmd.execute(manager, view);

    assertTrue("Should return true", result);
    assertTrue("Should show success", view.hasMessage("Copied"));
  }

  @Test
  public void testCopyEventsRangeCommandInvalidDateTime() throws IOException {
    CopyEventsRangeCommand cmd = new CopyEventsRangeCommand("bad", "bad", "Work", "bad");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasMessage("Invalid"));
  }

  @Test
  public void testCopyEventsRangeCommandEndBeforeStart() throws IOException {
    CopyEventsRangeCommand cmd = new CopyEventsRangeCommand(
        "2025-06-10", "2025-06-01", "Work", "2025-06-02");
    boolean result = cmd.execute(manager, view);

    assertFalse("Should return false", result);
    assertTrue("Should show error", view.hasMessage("End date must be after"));
  }
}
