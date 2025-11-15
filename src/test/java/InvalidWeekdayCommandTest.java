import static org.junit.Assert.assertTrue;

import calendar.command.CommandParser;
import calendar.controller.Controller;
import calendar.model.CalendarManager;
import calendar.model.CalendarModel;
import calendar.model.CalendarModelInterface;
import calendar.view.ConsoleView;
import calendar.view.ViewInterface;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.time.ZoneId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * TC 27: This test verifies that a command with weekdays does not allow invalid
 * characters for weekdays. Input: Character 'X'. Expected: IllegalArgumentException
 * or error message.
 */
public class InvalidWeekdayCommandTest {
  private CalendarManager manager;
  private CalendarModelInterface model;
  private ViewInterface view;
  private CommandParser parser;
  private ByteArrayOutputStream outContent;
  private PrintStream originalOut;

  /**
   * Sets up test fixtures.
   */
  @Before
  public void setUp() {
    manager = new CalendarManager();
    manager.createCalendar("TestCalendar", ZoneId.of("America/New_York"));
    manager.setCurrentCalendar("TestCalendar");
    model = manager.getCurrentCalendar().getModel();

    outContent = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outContent));
    view = new ConsoleView(System.out);
    parser = new CommandParser();
  }

  /**
   * Tears down test fixtures.
   */
  @After
  public void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  
  
  
  public void testInvalidWeekdayInSeriesCommand() throws IOException {
    String commands = "create event \"Meeting\" from 2025-06-02T10:00 to 2025-06-02T11:00 "
        + "repeats XYZ for 3 times\nexit\n";
    StringReader reader = new StringReader(commands);
    Controller controller = new Controller(manager, view, parser, reader, false);

    controller.run();

    String output = outContent.toString();
    
    assertTrue("Output should contain error about invalid weekday",
        output.toLowerCase().contains("invalid") || output.toLowerCase().contains("error"));
  }

  @Test
  
  public void testWeekdayFromCharInvalidCharacter() {
    try {
      calendar.model.Weekday.fromChar('X');
      assertTrue("Should have thrown IllegalArgumentException", false);
    } catch (IllegalArgumentException e) {
      assertTrue("Exception message should mention invalid weekday",
          e.getMessage().contains("Invalid weekday"));
    }
  }

  @Test
  
  public void testWeekdayParseStringInvalidCharacter() {
    try {
      calendar.model.Weekday.parseString("MXW");
      assertTrue("Should have thrown IllegalArgumentException", false);
    } catch (IllegalArgumentException e) {
      assertTrue("Exception message should mention invalid weekday",
          e.getMessage().contains("Invalid weekday"));
    }
  }
}
