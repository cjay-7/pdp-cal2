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
 * Tests Controller error path on invalid command.
 */
public class InteractiveControllerInvalidTest {
  private CalendarManager manager;
  private CalendarModelInterface model;
  private ViewInterface view;
  private CommandParser parser;
  private ByteArrayOutputStream out;
  private PrintStream originalOut;

  /**
   * Sets up model, parser, and output capture.
   */
  @Before
  public void setUp() {
    manager = new CalendarManager();
    manager.createCalendar("TestCalendar", ZoneId.of("America/New_York"));
    manager.setCurrentCalendar("TestCalendar");
    model = manager.getCurrentCalendar().getModel();

    parser = new CommandParser();
    originalOut = System.out;
    out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
    view = new ConsoleView(System.out);
  }

  /**
   * Restores System.out after each test.
   */
  @After
  public void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  public void testInvalidThenExitShowsError() throws IOException {
    String commands = "invalid command\nexit\n";
    StringReader reader = new StringReader(commands);
    new Controller(manager, view, parser, reader, true).run();
    String output = out.toString();
    assertTrue(output.contains("ERROR:") || output.toLowerCase().contains("invalid"));
  }
}
