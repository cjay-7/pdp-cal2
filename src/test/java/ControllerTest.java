import static org.junit.Assert.assertTrue;

import calendar.command.CommandParser;
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
 * Tests for controller classes.
 */
public class ControllerTest {
  private CalendarManager manager;
  private ViewInterface view;
  private CommandParser parser;
  private ByteArrayOutputStream outContent;
  private PrintStream originalOut;

  /**
   * Initializes manager, view, parser, and output capture.
   */
  @Before
  public void setUp() {
    manager = new CalendarManager();
    manager.createCalendar("TestCalendar", ZoneId.of("America/New_York"));
    manager.setCurrentCalendar("TestCalendar");

    outContent = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outContent));
    view = new ConsoleView(System.out);
    parser = new CommandParser();
  }

  /**
   * Restores System.out after each test.
   */
  @After
  public void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  public void testInteractiveControllerExitCommand() throws IOException {
    StringReader reader = new StringReader("exit\n");
    calendar.controller.Controller controller =
        new calendar.controller.Controller(manager, view, parser, reader, true);

    controller.run();

    String output = outContent.toString();
    assertTrue(output.contains("Enter commands"));
  }

  @Test
  // TC 25: This test verifies that the controller successfully sends the expected
  // inputs to the model. Input: Create event command. Expected: Model receives and
  // processes command, displays "Created event".
  public void testInteractiveControllerCreateAndExit() throws IOException {
    // Create a mock manager/model to verify delegation
    MockCalendarModel mockModel = new MockCalendarModel();
    MockCalendarManager mockManager = new MockCalendarManager(mockModel);

    String commands = "create event \"Test\" from 2025-06-01T10:00 to 2025-06-01T11:00\nexit\n";
    StringReader reader = new StringReader(commands);
    calendar.controller.Controller controller =
        new calendar.controller.Controller(mockManager, view, parser, reader, true);

    controller.run();

    // Verify the controller delegated to the model
    assertTrue("Controller should have called model.createEvent", mockModel.createEventCalled);
    String output = outContent.toString();
    assertTrue(output.contains("Created event"));
  }

  /**
   * Mock CalendarModel to verify controller delegation.
   */
  private static class MockCalendarModel implements calendar.model.CalendarModelInterface {
    boolean createEventCalled = false;

    @Override
    public boolean createEvent(calendar.model.EventInterface event) {
      createEventCalled = true;
      return true;
    }

    @Override
    public boolean createEventSeries(calendar.model.EventSeries series) {
      return false;
    }

    @Override
    public boolean editEvent(java.util.UUID eventId, calendar.model.EditSpec spec) {
      return false;
    }

    @Override
    public boolean editSeriesFrom(java.util.UUID seriesId, java.time.LocalDate fromDate,
                                  calendar.model.EditSpec spec) {
      return false;
    }

    @Override
    public boolean editEntireSeries(java.util.UUID seriesId, calendar.model.EditSpec spec) {
      return false;
    }

    @Override
    public java.util.List<calendar.model.EventInterface> getEventsOnDate(java.time.LocalDate date) {
      return java.util.Collections.emptyList();
    }

    @Override
    public java.util.List<calendar.model.EventInterface> getEventsInRange(
        java.time.LocalDateTime startDateTime, java.time.LocalDateTime endDateTime) {
      return java.util.Collections.emptyList();
    }

    @Override
    public java.util.List<calendar.model.EventInterface> getAllEvents() {
      return java.util.Collections.emptyList();
    }

    @Override
    public boolean isBusy(java.time.LocalDateTime dateTime) {
      return false;
    }

    @Override
    public void exportToCsv(java.nio.file.Path filePath) throws java.io.IOException {
    }

    @Override
    public calendar.model.EventInterface findEventById(java.util.UUID eventId) {
      return null;
    }

    @Override
    public calendar.model.EventInterface findEventByProperties(String subject,
        java.time.LocalDateTime startDateTime, java.time.LocalDateTime endDateTime) {
      return null;
    }
  }

  @Test
  public void testHeadlessControllerWithExit() throws IOException {
    String commands = "create event \"Test\" from 2025-06-01T10:00 to 2025-06-01T11:00\nexit\n";
    StringReader reader = new StringReader(commands);
    calendar.controller.Controller controller =
        new calendar.controller.Controller(manager, view, parser, reader, false);

    controller.run();

    String output = outContent.toString();
    assertTrue(output.contains("Created event"));
  }

  @Test
  public void testHeadlessControllerWithoutExit() throws IOException {
    String commands = "create event \"Test\" from 2025-06-01T10:00 to 2025-06-01T11:00\n";
    StringReader reader = new StringReader(commands);
    calendar.controller.Controller controller =
        new calendar.controller.Controller(manager, view, parser, reader, false);

    controller.run();

    String output = outContent.toString();
    assertTrue(output.contains("must end with 'exit'"));
  }

  @Test
  public void testHeadlessControllerEmptyLines() throws IOException {
    String commands = "create event \"Test\" from 2025-06-01T10:00 to 2025-06-01T11:00\n\n\nexit\n";
    StringReader reader = new StringReader(commands);
    calendar.controller.Controller controller =
        new calendar.controller.Controller(manager, view, parser, reader, false);

    controller.run();

    String output = outContent.toString();
    assertTrue(output.contains("Created event"));
  }

  @Test
  public void testHeadlessControllerInvalidCommand() throws IOException {
    String commands = "invalid command\nexit\n";
    StringReader reader = new StringReader(commands);
    calendar.controller.Controller controller =
        new calendar.controller.Controller(manager, view, parser, reader, false);

    controller.run();

    String output = outContent.toString();
    assertTrue(output.contains("ERROR:") || output.contains("Invalid"));
  }

  /**
   * Mock CalendarManager that wraps a mock model for testing.
   */
  private static class MockCalendarManager extends CalendarManager {
    private final calendar.model.Calendar mockCalendar;

    MockCalendarManager(MockCalendarModel mockModel) {
      super();
      this.mockCalendar = new calendar.model.Calendar("TestCalendar",
          ZoneId.of("America/New_York"), mockModel);
    }

    @Override
    public calendar.model.Calendar getCurrentCalendar() {
      return mockCalendar;
    }
  }
}

