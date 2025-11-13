import static org.junit.Assert.assertTrue;

import calendar.command.CreateAllDayEventSeriesCommand;
import calendar.command.CreateEventSeriesFromToCommand;
import calendar.model.CalendarManager;
import calendar.model.CalendarModel;
import calendar.model.CalendarModelInterface;
import calendar.view.ConsoleView;
import calendar.view.ViewInterface;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.ZoneId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for event series creation commands.
 */
public class CreateEventSeriesCommandTest {
  private CalendarManager manager;
  private CalendarModelInterface model;
  private ViewInterface view;
  private ByteArrayOutputStream outContent;

  /**
   * Sets up model, view, and output capture.
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

  @Test
  public void testCreateEventSeriesFromToWithOccurrences() throws IOException {
    CreateEventSeriesFromToCommand cmd = new CreateEventSeriesFromToCommand(
        "Standup", "2025-06-02T09:00", "2025-06-02T09:30", "MWF", 5, null, false);
    assertTrue(cmd.execute(manager, view));
  }

  @Test
  public void testCreateEventSeriesFromToWithEndDate() throws IOException {
    CreateEventSeriesFromToCommand cmd = new CreateEventSeriesFromToCommand(
        "Sync", "2025-06-03T14:00", "2025-06-03T15:00", "T", null, "2025-06-30", true);
    assertTrue(cmd.execute(manager, view));
  }

  @Test
  public void testCreateAllDayEventSeriesWithOccurrences() throws IOException {
    CreateAllDayEventSeriesCommand cmd = new CreateAllDayEventSeriesCommand(
        "Holiday", "2025-06-01", "M", 3, null, false);
    assertTrue(cmd.execute(manager, view));
  }

  @Test
  public void testCreateAllDayEventSeriesWithEndDate() throws IOException {
    CreateAllDayEventSeriesCommand cmd = new CreateAllDayEventSeriesCommand(
        "Meeting", "2025-06-01", "M", null, "2025-06-30", true);
    assertTrue(cmd.execute(manager, view));
  }
}

