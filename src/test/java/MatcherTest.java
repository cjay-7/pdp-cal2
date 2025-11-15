import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import calendar.command.CommandInterface;
import calendar.command.matchers.CopyEventCommandMatcher;
import calendar.command.matchers.CopyEventsOnDayCommandMatcher;
import calendar.command.matchers.CopyEventsRangeCommandMatcher;
import calendar.command.matchers.CreateAllDayEventCommandMatcher;
import calendar.command.matchers.CreateAllDayEventSeriesForCommandMatcher;
import calendar.command.matchers.CreateAllDayEventSeriesUntilCommandMatcher;
import calendar.command.matchers.CreateCalendarCommandMatcher;
import calendar.command.matchers.CreateEventCommandMatcher;
import calendar.command.matchers.CreateEventSeriesFromToForCommandMatcher;
import calendar.command.matchers.CreateEventSeriesFromToUntilCommandMatcher;
import calendar.command.matchers.EditCalendarCommandMatcher;
import calendar.command.matchers.EditEventCommandMatcher;
import calendar.command.matchers.EditEventsCommandMatcher;
import calendar.command.matchers.EditSeriesCommandMatcher;
import calendar.command.matchers.ExitCommandMatcher;
import calendar.command.matchers.ExportCommandMatcher;
import calendar.command.matchers.PrintAllEventsCommandMatcher;
import calendar.command.matchers.PrintEventsOnCommandMatcher;
import calendar.command.matchers.PrintEventsRangeCommandMatcher;
import calendar.command.matchers.ShowStatusCommandMatcher;
import calendar.command.matchers.UseCalendarCommandMatcher;
import calendar.model.CalendarManager;
import java.io.IOException;
import java.time.ZoneId;
import org.junit.Test;

/**
 * Comprehensive tests for all command matchers to achieve 100% mutation coverage.
 */
public class MatcherTest {

  @Test
  public void testCreateAllDayEventCommandMatcher() {
    CreateAllDayEventCommandMatcher matcher = new CreateAllDayEventCommandMatcher();

    
    CommandInterface cmd = matcher.tryMatch("create event \"Holiday\" on 2025-06-01");
    assertNotNull("Matcher should return non-null command for valid input", cmd);

    
    CommandInterface noMatch = matcher.tryMatch("invalid command");
    assertNull("Matcher should return null for invalid input", noMatch);

    
    cmd = matcher.tryMatch("create event 'Holiday' on 2025-06-01");
    assertNotNull("Should handle single quotes", cmd);

    
    cmd = matcher.tryMatch("create event Holiday on 2025-06-01");
    assertNotNull("Should handle no quotes", cmd);
  }

  @Test
  public void testCreateAllDayEventSeriesForCommandMatcher() {
    CreateAllDayEventSeriesForCommandMatcher matcher =
        new CreateAllDayEventSeriesForCommandMatcher();

    CommandInterface cmd = matcher.tryMatch(
        "create event \"Holiday\" on 2025-06-01 repeats M for 3 times");
    assertNotNull("Should return non-null command for valid input", cmd);

    CommandInterface noMatch = matcher.tryMatch("invalid");
    assertNull("Should return null for invalid input", noMatch);

    cmd = matcher.tryMatch(
        "create event 'Test' on 2025-06-01 repeats M for 5 times");
    assertNotNull("Should handle single quotes", cmd);
  }

  @Test
  public void testCreateAllDayEventSeriesUntilCommandMatcher() {
    CreateAllDayEventSeriesUntilCommandMatcher matcher =
        new CreateAllDayEventSeriesUntilCommandMatcher();

    CommandInterface cmd = matcher.tryMatch(
        "create event \"Meeting\" on 2025-06-01 repeats M until 2025-06-30");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("wrong format");
    assertNull("Should return null for invalid input", noMatch);
  }

  @Test
  public void testCreateCalendarCommandMatcher() {
    CreateCalendarCommandMatcher matcher = new CreateCalendarCommandMatcher();

    CommandInterface cmd = matcher.tryMatch(
        "create calendar --name \"Work\" --timezone America/New_York");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("create something else");
    assertNull("Should return null for invalid input", noMatch);
  }

  @Test
  public void testCreateEventCommandMatcher() {
    CreateEventCommandMatcher matcher = new CreateEventCommandMatcher();

    CommandInterface cmd = matcher.tryMatch(
        "create event \"Meeting\" from 2025-06-01T09:00 to 2025-06-01T10:00");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("invalid format");
    assertNull("Should return null for invalid input", noMatch);

    
    cmd = matcher.tryMatch(
        "create event \"Test Event\" from 2025-06-01T09:00 to 2025-06-01T10:00");
    assertNotNull("Should handle double quotes", cmd);

    
    cmd = matcher.tryMatch(
        "create event 'Test Event' from 2025-06-01T09:00 to 2025-06-01T10:00");
    assertNotNull("Should handle single quotes", cmd);

    
    cmd = matcher.tryMatch(
        "create event Meeting from 2025-06-01T09:00 to 2025-06-01T10:00");
    assertNotNull("Should handle no quotes", cmd);
  }

  @Test
  public void testCreateEventSeriesFromToForCommandMatcher() {
    CreateEventSeriesFromToForCommandMatcher matcher =
        new CreateEventSeriesFromToForCommandMatcher();

    CommandInterface cmd = matcher.tryMatch(
        "create event \"Standup\" from 2025-06-01T09:00 to 2025-06-01T09:30 "
        + "repeats MWF for 5 times");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("not a series command");
    assertNull("Should return null for invalid input", noMatch);
  }

  @Test
  public void testCreateEventSeriesFromToUntilCommandMatcher() {
    CreateEventSeriesFromToUntilCommandMatcher matcher =
        new CreateEventSeriesFromToUntilCommandMatcher();

    CommandInterface cmd = matcher.tryMatch(
        "create event \"Meeting\" from 2025-06-01T10:00 to 2025-06-01T11:00 "
        + "repeats T until 2025-06-30");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("wrong command");
    assertNull("Should return null for invalid input", noMatch);
  }

  @Test
  public void testEditCalendarCommandMatcher() {
    EditCalendarCommandMatcher matcher = new EditCalendarCommandMatcher();

    CommandInterface cmd = matcher.tryMatch(
        "edit calendar --name Work --property timezone America/Los_Angeles");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("not edit calendar");
    assertNull("Should return null for invalid input", noMatch);
  }

  @Test
  public void testEditEventCommandMatcher() {
    EditEventCommandMatcher matcher = new EditEventCommandMatcher();

    CommandInterface cmd = matcher.tryMatch(
        "edit event subject \"Meeting\" from 2025-06-01T09:00 to 2025-06-01T10:00 "
        + "with \"NewSubject\"");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("invalid edit");
    assertNull("Should return null for invalid input", noMatch);
  }

  @Test
  public void testEditEventsCommandMatcher() {
    EditEventsCommandMatcher matcher = new EditEventsCommandMatcher();

    CommandInterface cmd = matcher.tryMatch(
        "edit events start \"Standup\" from 2025-06-01T09:00 with 2025-06-01T08:30");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("not an edit events command");
    assertNull("Should return null for invalid input", noMatch);
  }

  @Test
  public void testEditSeriesCommandMatcher() {
    EditSeriesCommandMatcher matcher = new EditSeriesCommandMatcher();

    CommandInterface cmd = matcher.tryMatch(
        "edit series location \"Standup\" from 2025-06-01T09:00 with \"Room A\"");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("different command");
    assertNull("Should return null for invalid input", noMatch);
  }

  @Test
  public void testExitCommandMatcher() {
    ExitCommandMatcher matcher = new ExitCommandMatcher();

    CommandInterface cmd = matcher.tryMatch("exit");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("not exit");
    assertNull("Should return null for non-exit command", noMatch);
  }

  @Test
  public void testExportCommandMatcher() {
    ExportCommandMatcher matcher = new ExportCommandMatcher();

    CommandInterface cmd = matcher.tryMatch("export cal calendar.csv");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("import something");
    assertNull("Should return null for invalid input", noMatch);
  }

  @Test
  public void testPrintAllEventsCommandMatcher() {
    PrintAllEventsCommandMatcher matcher = new PrintAllEventsCommandMatcher();

    CommandInterface cmd = matcher.tryMatch("print all events");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("print some events");
    assertNull("Should return null for invalid input", noMatch);
  }

  @Test
  public void testPrintEventsOnCommandMatcher() {
    PrintEventsOnCommandMatcher matcher = new PrintEventsOnCommandMatcher();

    CommandInterface cmd = matcher.tryMatch("print events on 2025-06-01");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("print events");
    assertNull("Should return null for invalid input", noMatch);
  }

  @Test
  public void testPrintEventsRangeCommandMatcher() {
    PrintEventsRangeCommandMatcher matcher = new PrintEventsRangeCommandMatcher();

    CommandInterface cmd = matcher.tryMatch(
        "print events from 2025-06-01T00:00 to 2025-06-30T23:59");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("print events on date");
    assertNull("Should return null for invalid input", noMatch);
  }

  @Test
  public void testShowStatusCommandMatcher() {
    ShowStatusCommandMatcher matcher = new ShowStatusCommandMatcher();

    CommandInterface cmd = matcher.tryMatch("show status on 2025-06-01T10:00");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("show something else");
    assertNull("Should return null for invalid input", noMatch);
  }

  @Test
  public void testUseCalendarCommandMatcher() {
    UseCalendarCommandMatcher matcher = new UseCalendarCommandMatcher();

    CommandInterface cmd = matcher.tryMatch("use calendar --name \"Work\"");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("use something");
    assertNull("Should return null for invalid input", noMatch);
  }

  @Test
  public void testCopyEventCommandMatcher() {
    CopyEventCommandMatcher matcher = new CopyEventCommandMatcher();

    CommandInterface cmd = matcher.tryMatch(
        "copy event \"Meeting\" on 2025-06-01T09:00 --target Work to 2025-06-02T09:00");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("copy something else");
    assertNull("Should return null for invalid input", noMatch);
  }

  @Test
  public void testCopyEventsOnDayCommandMatcher() {
    CopyEventsOnDayCommandMatcher matcher = new CopyEventsOnDayCommandMatcher();

    CommandInterface cmd = matcher.tryMatch(
        "copy events on 2025-06-01 --target Work to 2025-06-02");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("copy event");
    assertNull("Should return null for invalid input", noMatch);
  }

  @Test
  public void testCopyEventsRangeCommandMatcher() {
    CopyEventsRangeCommandMatcher matcher = new CopyEventsRangeCommandMatcher();

    CommandInterface cmd = matcher.tryMatch(
        "copy events between 2025-06-01T09:00 and 2025-06-01T17:00 "
        + "--target Work to 2025-06-02T09:00");
    assertNotNull("Should return non-null command", cmd);

    CommandInterface noMatch = matcher.tryMatch("copy events on");
    assertNull("Should return null for invalid input", noMatch);
  }

  
  @Test
  public void testCreateAllDayEventMatcherExecutesWithQuotes() throws IOException {
    CalendarManager manager = new CalendarManager();
    manager.createCalendar("Test", ZoneId.systemDefault());
    manager.setCurrentCalendar("Test");
    TestView view = new TestView();

    CreateAllDayEventCommandMatcher matcher = new CreateAllDayEventCommandMatcher();

    
    CommandInterface cmd = matcher.tryMatch("create event \"Holiday\" on 2025-06-01");
    assertNotNull(cmd);
    assertTrue("Command with double quotes should execute", cmd.execute(manager, view));
    view.clear();

    
    cmd = matcher.tryMatch("create event 'Vacation' on 2025-06-02");
    assertNotNull(cmd);
    assertTrue("Command with single quotes should execute", cmd.execute(manager, view));
    view.clear();

    
    cmd = matcher.tryMatch("create event Meeting on 2025-06-03");
    assertNotNull(cmd);
    assertTrue("Command without quotes should execute", cmd.execute(manager, view));
  }

  @Test
  public void testCreateEventMatcherExecutesWithQuotes() throws IOException {
    CalendarManager manager = new CalendarManager();
    manager.createCalendar("Test", ZoneId.systemDefault());
    manager.setCurrentCalendar("Test");
    TestView view = new TestView();

    CreateEventCommandMatcher matcher = new CreateEventCommandMatcher();

    
    CommandInterface cmd = matcher.tryMatch(
        "create event \"Meeting\" from 2025-06-01T09:00 to 2025-06-01T10:00");
    assertNotNull(cmd);
    assertTrue("Command with double quotes should execute",
        cmd.execute(manager, view));
    view.clear();

    
    cmd = matcher.tryMatch(
        "create event 'Standup' from 2025-06-01T11:00 to 2025-06-01T11:30");
    assertNotNull(cmd);
    assertTrue("Command with single quotes should execute",
        cmd.execute(manager, view));
    view.clear();

    
    cmd = matcher.tryMatch(
        "create event Lunch from 2025-06-01T12:00 to 2025-06-01T13:00");
    assertNotNull(cmd);
    assertTrue("Command without quotes should execute", cmd.execute(manager, view));
  }

  @Test
  public void testEditEventMatcherExecutesWithQuotes() throws IOException {
    CalendarManager manager = new CalendarManager();
    manager.createCalendar("Test", ZoneId.systemDefault());
    manager.setCurrentCalendar("Test");
    TestView view = new TestView();

    
    CreateEventCommandMatcher createMatcher = new CreateEventCommandMatcher();
    CommandInterface createCmd = createMatcher.tryMatch(
        "create event \"OldName\" from 2025-06-01T09:00 to 2025-06-01T10:00");
    createCmd.execute(manager, view);
    view.clear();

    EditEventCommandMatcher matcher = new EditEventCommandMatcher();

    
    CommandInterface cmd = matcher.tryMatch(
        "edit event subject \"OldName\" from 2025-06-01T09:00 to 2025-06-01T10:00 "
        + "with \"NewName\"");
    assertNotNull(cmd);
    assertTrue("Edit command with double quotes should execute",
        cmd.execute(manager, view));
  }

  @Test
  public void testEditEventsMatcherExecutesWithQuotes() throws IOException {
    CalendarManager manager = new CalendarManager();
    manager.createCalendar("Test", ZoneId.systemDefault());
    manager.setCurrentCalendar("Test");
    TestView view = new TestView();

    
    CreateEventCommandMatcher createMatcher = new CreateEventCommandMatcher();
    CommandInterface createCmd = createMatcher.tryMatch(
        "create event \"TestEvent\" from 2025-06-01T09:00 to 2025-06-01T10:00");
    createCmd.execute(manager, view);
    view.clear();

    EditEventsCommandMatcher matcher = new EditEventsCommandMatcher();

    
    CommandInterface cmd = matcher.tryMatch(
        "edit events subject \"TestEvent\" from 2025-06-01T09:00 with \"UpdatedEvent\"");
    assertNotNull(cmd);
    assertTrue("Edit events command with double quotes should execute",
        cmd.execute(manager, view));
  }

  @Test
  public void testEditSeriesMatcherExecutesWithQuotes() throws IOException {
    CalendarManager manager = new CalendarManager();
    manager.createCalendar("Test", ZoneId.systemDefault());
    manager.setCurrentCalendar("Test");
    TestView view = new TestView();

    
    CreateEventSeriesFromToForCommandMatcher createMatcher =
        new CreateEventSeriesFromToForCommandMatcher();
    CommandInterface createCmd = createMatcher.tryMatch(
        "create event \"SeriesEvent\" from 2025-06-02T09:00 to 2025-06-02T09:30 "
        + "repeats MWF for 5 times");
    createCmd.execute(manager, view);
    view.clear();

    EditSeriesCommandMatcher matcher = new EditSeriesCommandMatcher();

    
    CommandInterface cmd = matcher.tryMatch(
        "edit series subject \"SeriesEvent\" from 2025-06-02T09:00 with \"UpdatedSeries\"");
    assertNotNull(cmd);
    assertTrue("Edit series command with double quotes should execute",
        cmd.execute(manager, view));
  }

  @Test
  public void testCreateEventSeriesMatchersExecuteWithQuotes() throws IOException {
    CalendarManager manager = new CalendarManager();
    manager.createCalendar("Test", ZoneId.systemDefault());
    manager.setCurrentCalendar("Test");
    TestView view = new TestView();

    
    CreateAllDayEventSeriesForCommandMatcher matcher1 =
        new CreateAllDayEventSeriesForCommandMatcher();
    CommandInterface cmd = matcher1.tryMatch(
        "create event \"MondayHoliday\" on 2025-06-02 repeats M for 3 times");
    assertNotNull(cmd);
    assertTrue("All-day series command with double quotes should execute",
        cmd.execute(manager, view));
    view.clear();

    
    CreateAllDayEventSeriesUntilCommandMatcher matcher2 =
        new CreateAllDayEventSeriesUntilCommandMatcher();
    cmd = matcher2.tryMatch(
        "create event \"WeekendDay\" on 2025-06-07 repeats S until 2025-06-30");
    assertNotNull(cmd);
    assertTrue("All-day series until command with double quotes should execute",
        cmd.execute(manager, view));
    view.clear();

    
    CreateEventSeriesFromToForCommandMatcher matcher3 =
        new CreateEventSeriesFromToForCommandMatcher();
    cmd = matcher3.tryMatch(
        "create event \"DailyStandup\" from 2025-06-04T09:00 to 2025-06-04T09:30 "
        + "repeats W for 4 times");
    assertNotNull(cmd);
    assertTrue("Event series from-to for command with double quotes should execute",
        cmd.execute(manager, view));
    view.clear();

    
    CreateEventSeriesFromToUntilCommandMatcher matcher4 =
        new CreateEventSeriesFromToUntilCommandMatcher();
    cmd = matcher4.tryMatch(
        "create event \"WeeklyReview\" from 2025-06-05T14:00 to 2025-06-05T15:00 "
        + "repeats R until 2025-06-30");
    assertNotNull(cmd);
    assertTrue("Event series from-to until command with double quotes should execute",
        cmd.execute(manager, view));
  }
}
