import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import calendar.command.CommandInterface;
import calendar.command.CommandParser;
import calendar.command.CreateEventCommand;
import calendar.command.EditEventCommand;
import calendar.command.ExitCommand;
import calendar.command.NoOpCommand;
import calendar.command.PrintAllEventsCommand;
import org.junit.Before;
import org.junit.Test;

/**
 * Comprehensive tests for CommandParser.
 */
public class CommandParserTest {
  private CommandParser parser;

  /**
   * Initializes a new parser before each test.
   */
  @Before
  public void setUp() {
    parser = new CommandParser();
  }

  @Test
  public void testParseExit() {
    CommandInterface cmd = parser.parse("exit");
    assertTrue(cmd instanceof ExitCommand);
  }

  @Test
  public void testParseExitCaseInsensitive() {
    CommandInterface cmd = parser.parse("EXIT");
    assertTrue(cmd instanceof ExitCommand);
  }

  @Test
  public void testParseEmptyString() {
    CommandInterface cmd = parser.parse("");
    assertTrue(cmd instanceof NoOpCommand);
  }

  @Test
  public void testParseWhitespace() {
    CommandInterface cmd = parser.parse("   ");
    assertTrue(cmd instanceof NoOpCommand);
  }

  @Test
  public void testParseNull() {
    CommandInterface cmd = parser.parse(null);
    assertTrue(cmd instanceof NoOpCommand);
  }

  @Test
  
  public void testParseCreateEvent() {
    CommandInterface cmd =
        parser.parse("create event \"Meeting\" from 2025-06-01T09:00 to 2025-06-01T10:00");
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testParseCreateEventWithQuotes() {
    CommandInterface cmd =
        parser.parse("create event 'Team Sync' from 2025-06-01T09:00 to 2025-06-01T10:00");
    assertTrue(cmd instanceof CreateEventCommand);
  }

  @Test
  public void testParseCreateAllDayEvent() {
    CommandInterface cmd = parser.parse("create event \"Holiday\" on 2025-06-01");
    assertNotNull(cmd);
  }

  @Test
  public void testParsePrintAllEvents() {
    CommandInterface cmd = parser.parse("print all events");
    assertTrue(cmd instanceof PrintAllEventsCommand);
  }

  @Test
  public void testParsePrintEventsOn() {
    CommandInterface cmd = parser.parse("print events on 2025-06-01");
    assertNotNull(cmd);
  }

  @Test
  public void testParsePrintEventsRange() {
    CommandInterface cmd = parser.parse("print events from 2025-06-01T00:00 to 2025-06-30T23:59");
    assertNotNull(cmd);
  }

  @Test
  public void testParseShowStatus() {
    CommandInterface cmd = parser.parse("show status on 2025-06-01T10:00");
    assertNotNull(cmd);
  }

  @Test
  public void testParseEditEvent() {
    CommandInterface cmd = parser.parse(
        "edit event subject \"Meeting\" from 2025-06-01T09:00 to 2025-06-01T10:00 with "
        + "\"New Name\"");
    assertTrue(cmd instanceof EditEventCommand);
  }

  @Test
  public void testParseEditEvents() {
    CommandInterface cmd =
        parser.parse("edit events start \"Standup\" from 2025-06-01T09:00 with 2025-06-01T08:30");
    assertNotNull(cmd);
  }

  @Test
  public void testParseEditSeries() {
    CommandInterface cmd =
        parser.parse("edit series location \"Standup\" from 2025-06-01T09:00 with \"Room A\"");
    assertNotNull(cmd);
  }

  @Test
  public void testParseExport() {
    CommandInterface cmd = parser.parse("export cal calendar.csv");
    assertNotNull(cmd);
  }

  @Test
  public void testParseCreateSeriesFromToFor() {
    CommandInterface cmd = parser.parse(
        "create event \"Standup\" from 2025-06-01T09:00 to 2025-06-01T09:30 repeats MWF "
        + "for 5 times");
    assertNotNull(cmd);
  }

  @Test
  public void testParseCreateSeriesFromToUntil() {
    CommandInterface cmd = parser.parse(
        "create event \"Meeting\" from 2025-06-01T10:00 to 2025-06-01T11:00 repeats T "
        + "until 2025-06-30");
    assertNotNull(cmd);
  }

  @Test
  public void testParseCreateAllDaySeriesFor() {
    CommandInterface cmd =
        parser.parse("create event \"Holiday\" on 2025-06-01 repeats M for 3 times");
    assertNotNull(cmd);
  }

  @Test
  public void testParseCreateAllDaySeriesUntil() {
    CommandInterface cmd =
        parser.parse("create event \"Meeting\" on 2025-06-01 repeats M until 2025-06-30");
    assertNotNull(cmd);
  }

  @Test
  
  
  
  
  public void testParseUnrecognizedCommand() {
    CommandInterface cmd = parser.parse("invalid command syntax");
    assertTrue(cmd instanceof NoOpCommand);
  }

  @Test
  public void testStripQuotesDoubleQuotes() {
    
    CommandInterface cmd =
        parser.parse("create event \"Test Meeting\" from 2025-06-01T09:00 to 2025-06-01T10:00");
    assertNotNull(cmd);
  }

  @Test
  public void testStripQuotesSingleQuotes() {
    CommandInterface cmd =
        parser.parse("create event 'Test Meeting' from 2025-06-01T09:00 to 2025-06-01T10:00");
    assertNotNull(cmd);
  }

  @Test
  public void testStripQuotesNoQuotes() {
    CommandInterface cmd =
        parser.parse("create event TestMeeting from 2025-06-01T09:00 to 2025-06-01T10:00");
    assertNotNull(cmd);
  }
}

