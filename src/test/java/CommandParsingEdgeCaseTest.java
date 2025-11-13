import static org.junit.Assert.*;

import calendar.command.*;
import calendar.exceptions.InvalidCommandException;
import java.io.IOException;
import org.junit.Test;

/**
 * Edge case tests for command parsing to achieve 100% branch coverage.
 * Tests all possible command variations, edge cases, and error conditions.
 */
public class CommandParsingEdgeCaseTest {
  private CommandParser parser = new CommandParser();

  // ========== Calendar Management Commands ==========

  @Test
  public void testCreateCalendarValidTimezones() throws InvalidCommandException {
    CommandInterface cmd1 = parser.parse("create calendar --name Work --timezone America/New_York");
    assertTrue(cmd1 instanceof CreateCalendarCommand);

    CommandInterface cmd2 = parser.parse("create calendar --name Personal --timezone Europe/London");
    assertTrue(cmd2 instanceof CreateCalendarCommand);

    CommandInterface cmd3 = parser.parse("create calendar --name School --timezone Asia/Tokyo");
    assertTrue(cmd3 instanceof CreateCalendarCommand);
  }

  @Test(expected = InvalidCommandException.class)
  public void testCreateCalendarMissingName() throws InvalidCommandException {
    parser.parse("create calendar --timezone America/New_York");
  }

  @Test(expected = InvalidCommandException.class)
  public void testCreateCalendarMissingTimezone() throws InvalidCommandException {
    parser.parse("create calendar --name Work");
  }

  @Test
  public void testUseCalendarCommand() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("use calendar --name Work");
    assertTrue(cmd instanceof UseCalendarCommand);
  }

  @Test(expected = InvalidCommandException.class)
  public void testUseCalendarMissingName() throws InvalidCommandException {
    parser.parse("use calendar");
  }

  @Test
  public void testEditCalendarName() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("edit calendar --name Work --property name NewWork");
    assertTrue(cmd instanceof EditCalendarCommand);
  }

  @Test
  public void testEditCalendarTimezone() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("edit calendar --name Work --property timezone America/Chicago");
    assertTrue(cmd instanceof EditCalendarCommand);
  }

  @Test(expected = InvalidCommandException.class)
  public void testEditCalendarMissingProperty() throws InvalidCommandException {
    parser.parse("edit calendar --name Work");
  }

  // ========== Event Creation Commands ==========

  @Test
  public void testCreateEventWithQuotedSubject() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"Team Meeting\" from 2025-06-01T09:00 to 2025-06-01T10:00");
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testCreateEventWithSingleWord() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event Meeting from 2025-06-01T09:00 to 2025-06-01T10:00");
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testCreateAllDayEvent() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"Holiday\" on 2025-12-25");
    assertTrue(cmd instanceof CreateAllDayEventCommand);
  }

  @Test
  public void testCreateSeriesWithForCount() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"Standup\" from 2025-06-02T09:00 to 2025-06-02T09:30 repeats MWF for 10 times");
    assertTrue(cmd instanceof CreateEventSeriesFromToCommand);
  }

  @Test
  public void testCreateSeriesWithUntilDate() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"Weekly\" from 2025-06-02T15:00 to 2025-06-02T16:00 repeats T until 2025-12-31");
    assertTrue(cmd instanceof CreateEventSeriesFromToCommand);
  }

  @Test
  public void testCreateAllDaySeriesFor() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"Training\" on 2025-06-02 repeats R for 4 times");
    assertTrue(cmd instanceof CreateAllDayEventSeriesCommand);
  }

  @Test
  public void testCreateAllDaySeriesUntil() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"Gym\" on 2025-06-02 repeats MWF until 2025-08-31");
    assertTrue(cmd instanceof CreateAllDayEventSeriesCommand);
  }

  @Test(expected = InvalidCommandException.class)
  public void testCreateEventInvalidDateFormat() throws InvalidCommandException {
    parser.parse("create event \"Test\" from 2025/06/01T09:00 to 2025-06-01T10:00");
  }

  @Test(expected = InvalidCommandException.class)
  public void testCreateEventMissingEndTime() throws InvalidCommandException {
    parser.parse("create event \"Test\" from 2025-06-01T09:00");
  }

  @Test(expected = InvalidCommandException.class)
  public void testCreateEventEndBeforeStart() throws InvalidCommandException {
    // This should be caught by validation
    CommandInterface cmd = parser.parse("create event \"Invalid\" from 2025-06-01T10:00 to 2025-06-01T09:00");
    // Will throw when executed
  }

  // ========== Query Commands ==========

  @Test
  public void testPrintAllEvents() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("print all events");
    assertTrue(cmd instanceof PrintAllEventsCommand);
  }

  @Test
  public void testPrintEventsOn() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("print events on 2025-06-01");
    assertTrue(cmd instanceof PrintEventsOnCommand);
  }

  @Test
  public void testPrintEventsFromTo() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("print events from 2025-06-01T00:00 to 2025-06-30T23:59");
    assertTrue(cmd instanceof PrintEventsRangeCommand);
  }

  @Test
  public void testShowStatus() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("show status on 2025-06-01T10:00");
    assertTrue(cmd instanceof ShowStatusCommand);
  }

  @Test(expected = InvalidCommandException.class)
  public void testPrintInvalidCommand() throws InvalidCommandException {
    parser.parse("print something invalid");
  }

  // ========== Edit Commands ==========

  @Test
  public void testEditEventSubject() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("edit event subject \"Old\" from 2025-06-01T10:00 to 2025-06-01T11:00 with \"New\"");
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test
  public void testEditEventDescription() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("edit event description \"Meeting\" from 2025-06-01T10:00 to 2025-06-01T11:00 with \"Important meeting\"");
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test
  public void testEditEventLocation() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("edit event location \"Meeting\" from 2025-06-01T10:00 to 2025-06-01T11:00 with \"Room 101\"");
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test
  public void testEditEventPrivate() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("edit event private \"Meeting\" from 2025-06-01T10:00 to 2025-06-01T11:00 with \"true\"");
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test
  public void testEditEventsFrom() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("edit events location \"Standup\" from 2025-06-09T09:00 with \"Room A\"");
    assertTrue(cmd instanceof EditEventsCommand);
  }

  @Test
  public void testEditSeries() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("edit series description \"Weekly\" from 2025-06-02T15:00 with \"Important\"");
    assertTrue(cmd instanceof EditSeriesCommand);
  }

  @Test(expected = InvalidCommandException.class)
  public void testEditEventInvalidProperty() throws InvalidCommandException {
    parser.parse("edit event invalid \"Test\" from 2025-06-01T10:00 to 2025-06-01T11:00 with \"value\"");
  }

  @Test(expected = InvalidCommandException.class)
  public void testEditEventMissingWith() throws InvalidCommandException {
    parser.parse("edit event subject \"Test\" from 2025-06-01T10:00 to 2025-06-01T11:00");
  }

  // ========== Copy Commands ==========

  @Test
  public void testCopyEventTo() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("copy event Meeting on 2025-06-01T10:00 --target Personal to 2025-06-15T10:00");
    assertTrue(cmd instanceof CopyEventCommand);
  }

  @Test
  public void testCopyEventsOnDate() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("copy events on 2025-06-01 --target Personal to 2025-07-01");
    assertTrue(cmd instanceof CopyEventsOnDateCommand);
  }

  @Test
  public void testCopyEventsBetween() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("copy events between 2025-06-01 and 2025-06-30 --target School to 2025-09-01");
    assertTrue(cmd instanceof CopyEventsRangeCommand);
  }

  @Test(expected = InvalidCommandException.class)
  public void testCopyEventMissingTarget() throws InvalidCommandException {
    parser.parse("copy event Meeting on 2025-06-01T10:00 to 2025-06-15T10:00");
  }

  @Test(expected = InvalidCommandException.class)
  public void testCopyEventsMissingTarget() throws InvalidCommandException {
    parser.parse("copy events on 2025-06-01 to 2025-07-01");
  }

  // ========== Export Commands ==========

  @Test
  public void testExportCSV() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("export cal calendar.csv");
    assertTrue(cmd instanceof ExportCommand);
  }

  @Test
  public void testExportIcal() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("export cal calendar.ical");
    assertTrue(cmd instanceof ExportCommand);
  }

  @Test(expected = InvalidCommandException.class)
  public void testExportMissingFilename() throws InvalidCommandException {
    parser.parse("export cal");
  }

  // ========== Exit and NoOp ==========

  @Test
  public void testExitCommand() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("exit");
    assertTrue(cmd instanceof ExitCommand);
  }

  @Test
  public void testExitCommandUpperCase() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("EXIT");
    assertTrue(cmd instanceof ExitCommand);
  }

  @Test
  public void testEmptyCommand() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("");
    assertTrue(cmd instanceof NoOpCommand);
  }

  @Test
  public void testWhitespaceCommand() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("   ");
    assertTrue(cmd instanceof NoOpCommand);
  }

  @Test
  public void testTabCommand() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("\t\t");
    assertTrue(cmd instanceof NoOpCommand);
  }

  // ========== Invalid Commands ==========

  @Test(expected = InvalidCommandException.class)
  public void testInvalidCommand() throws InvalidCommandException {
    parser.parse("this is not a valid command");
  }

  @Test(expected = InvalidCommandException.class)
  public void testPartialCommand() throws InvalidCommandException {
    parser.parse("create");
  }

  @Test(expected = InvalidCommandException.class)
  public void testMisspelledCommand() throws InvalidCommandException {
    parser.parse("crete event \"Test\" from 2025-06-01T10:00 to 2025-06-01T11:00");
  }

  // ========== Weekday Parsing Edge Cases ==========

  @Test
  public void testSingleWeekday() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"Monday\" from 2025-06-02T09:00 to 2025-06-02T10:00 repeats M for 5 times");
    assertTrue(cmd instanceof CreateEventSeriesFromToCommand);
  }

  @Test
  public void testAllWeekdays() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"Daily\" from 2025-06-02T09:00 to 2025-06-02T10:00 repeats MTWRFSU for 7 times");
    assertTrue(cmd instanceof CreateEventSeriesFromToCommand);
  }

  @Test
  public void testWeekdaysCaseInsensitive() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"Test\" from 2025-06-02T09:00 to 2025-06-02T10:00 repeats mwf for 3 times");
    assertTrue(cmd instanceof CreateEventSeriesFromToCommand);
  }

  @Test(expected = InvalidCommandException.class)
  public void testInvalidWeekday() throws InvalidCommandException {
    parser.parse("create event \"Test\" from 2025-06-02T09:00 to 2025-06-02T10:00 repeats X for 3 times");
  }

  // ========== Special Characters in Event Names ==========

  @Test
  public void testEventNameWithSpecialCharacters() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"Meeting: Q1 Review (Important!)\" from 2025-06-01T10:00 to 2025-06-01T11:00");
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testEventNameWithNumbers() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"CS5010 Lecture #5\" from 2025-06-01T10:00 to 2025-06-01T11:00");
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testEventNameWithApostrophe() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"John's Meeting\" from 2025-06-01T10:00 to 2025-06-01T11:00");
    assertTrue(cmd instanceof CreateEventCommand);
  }

  // ========== Edge Case Times ==========

  @Test
  public void testMidnightTime() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"Midnight\" from 2025-06-01T00:00 to 2025-06-01T01:00");
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testEndOfDayTime() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"Late\" from 2025-06-01T23:00 to 2025-06-01T23:59");
    assertTrue(cmd instanceof CreateEventCommand);
  }

  // ========== Long Event Names ==========

  @Test
  public void testLongEventName() throws InvalidCommandException {
    String longName = "This is a very long event name that contains many words and should still be parsed correctly by the command parser without any issues";
    CommandInterface cmd = parser.parse("create event \"" + longName + "\" from 2025-06-01T10:00 to 2025-06-01T11:00");
    assertTrue(cmd instanceof CreateEventCommand);
  }

  // ========== Multiple Spaces ==========

  @Test
  public void testCommandWithMultipleSpaces() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create   event   \"Test\"   from   2025-06-01T10:00   to   2025-06-01T11:00");
    assertTrue(cmd instanceof CreateEventCommand);
  }

  // ========== Case Insensitivity ==========

  @Test
  public void testCommandCaseInsensitivity() throws InvalidCommandException {
    CommandInterface cmd1 = parser.parse("CREATE EVENT \"Test\" FROM 2025-06-01T10:00 TO 2025-06-01T11:00");
    assertTrue(cmd1 instanceof CreateEventCommand);

    CommandInterface cmd2 = parser.parse("PRINT ALL EVENTS");
    assertTrue(cmd2 instanceof PrintAllEventsCommand);

    CommandInterface cmd3 = parser.parse("EXIT");
    assertTrue(cmd3 instanceof ExitCommand);
  }

  // ========== Boundary Dates ==========

  @Test
  public void testLeapYearDate() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"LeapDay\" on 2024-02-29");
    assertTrue(cmd instanceof CreateAllDayEventCommand);
  }

  @Test
  public void testFirstDayOfYear() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"NewYear\" on 2025-01-01");
    assertTrue(cmd instanceof CreateAllDayEventCommand);
  }

  @Test
  public void testLastDayOfYear() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"NewYearEve\" on 2025-12-31");
    assertTrue(cmd instanceof CreateAllDayEventCommand);
  }

  // ========== Series Edge Cases ==========

  @Test
  public void testSeriesWithSingleOccurrence() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"Once\" from 2025-06-02T09:00 to 2025-06-02T10:00 repeats M for 1 times");
    assertTrue(cmd instanceof CreateEventSeriesFromToCommand);
  }

  @Test
  public void testSeriesWithManyOccurrences() throws InvalidCommandException {
    CommandInterface cmd = parser.parse("create event \"Many\" from 2025-06-02T09:00 to 2025-06-02T10:00 repeats M for 100 times");
    assertTrue(cmd instanceof CreateEventSeriesFromToCommand);
  }
}
