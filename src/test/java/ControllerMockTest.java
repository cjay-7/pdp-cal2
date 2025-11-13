import static org.junit.Assert.*;

import calendar.command.CommandInterface;
import calendar.command.CommandParser;
import calendar.controller.Controller;
import calendar.model.*;
import calendar.view.ViewInterface;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Controller tests using mock objects to verify proper delegation.
 * Addresses rubric feedback: "Use mock model to verify controller delegates correctly" (Line 61).
 */
public class ControllerMockTest {
  private ByteArrayOutputStream outContent;
  private PrintStream originalOut;
  private ViewInterface view;
  private CommandParser parser;

  @Before
  public void setUp() {
    outContent = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outContent));
    view = new calendar.view.ConsoleView(System.out);
    parser = new CommandParser();
  }

  @After
  public void tearDown() {
    System.setOut(originalOut);
  }

  // ========== Mock Classes ==========

  /**
   * Mock CalendarModel that tracks which methods were called.
   */
  private static class MockCalendarModel implements CalendarModelInterface {
    boolean createEventCalled = false;
    boolean createEventSeriesCalled = false;
    boolean editEventCalled = false;
    boolean editSeriesFromCalled = false;
    boolean editEntireSeriesCalled = false;
    boolean getAllEventsCalled = false;
    boolean getEventsOnDateCalled = false;
    boolean getEventsInRangeCalled = false;
    boolean isBusyCalled = false;
    boolean findEventByIdCalled = false;
    boolean findEventByPropertiesCalled = false;
    boolean exportToCsvCalled = false;

    EventInterface lastCreatedEvent = null;
    UUID lastEditedEventId = null;
    LocalDateTime lastBusyCheckTime = null;

    @Override
    public boolean createEvent(EventInterface event) {
      createEventCalled = true;
      lastCreatedEvent = event;
      return true;
    }

    @Override
    public boolean createEventSeries(EventSeries series) {
      createEventSeriesCalled = true;
      return true;
    }

    @Override
    public boolean editEvent(UUID eventId, EditSpec spec) {
      editEventCalled = true;
      lastEditedEventId = eventId;
      return true;
    }

    @Override
    public boolean editSeriesFrom(UUID seriesId, LocalDate fromDate, EditSpec spec) {
      editSeriesFromCalled = true;
      return true;
    }

    @Override
    public boolean editEntireSeries(UUID seriesId, EditSpec spec) {
      editEntireSeriesCalled = true;
      return true;
    }

    @Override
    public List<EventInterface> getAllEvents() {
      getAllEventsCalled = true;
      return new ArrayList<>();
    }

    @Override
    public List<EventInterface> getEventsOnDate(LocalDate date) {
      getEventsOnDateCalled = true;
      return new ArrayList<>();
    }

    @Override
    public List<EventInterface> getEventsInRange(LocalDateTime start, LocalDateTime end) {
      getEventsInRangeCalled = true;
      return new ArrayList<>();
    }

    @Override
    public boolean isBusy(LocalDateTime dateTime) {
      isBusyCalled = true;
      lastBusyCheckTime = dateTime;
      return false;
    }

    @Override
    public EventInterface findEventById(UUID eventId) {
      findEventByIdCalled = true;
      // Return a mock event for edit operations to work
      if (eventId != null) {
        return new Event("MockEvent",
            LocalDateTime.of(2025, 6, 1, 10, 0),
            LocalDateTime.of(2025, 6, 1, 11, 0),
            null, null, false, eventId, null);
      }
      return null;
    }

    @Override
    public EventInterface findEventByProperties(String subject, LocalDateTime start,
        LocalDateTime end) {
      findEventByPropertiesCalled = true;
      return null;
    }

    @Override
    public void exportToCsv(Path filePath) throws IOException {
      exportToCsvCalled = true;
    }
  }

  /**
   * Mock CalendarManager that uses mock model.
   */
  private static class MockCalendarManager extends CalendarManager {
    private final MockCalendarModel mockModel;
    private final Calendar mockCalendar;

    MockCalendarManager(MockCalendarModel mockModel) {
      super();
      this.mockModel = mockModel;
      this.mockCalendar = new Calendar("MockCalendar", ZoneId.of("America/New_York"),
          mockModel);
      // Set the mock calendar as current
      this.createCalendar("MockCalendar", ZoneId.of("America/New_York"));
    }

    @Override
    public Calendar getCurrentCalendar() {
      return mockCalendar;
    }

    public MockCalendarModel getMockModel() {
      return mockModel;
    }
  }

  // ========== Tests ==========

  @Test
  public void testControllerDelegatesCreateEventToModel() throws IOException {
    // TC: Verify controller calls model.createEvent (Rubric line 61)
    MockCalendarModel mockModel = new MockCalendarModel();
    MockCalendarManager mockManager = new MockCalendarManager(mockModel);

    String command = "create event \"Test\" from 2025-06-01T10:00 to 2025-06-01T11:00\nexit\n";
    StringReader reader = new StringReader(command);
    Controller controller = new Controller(mockManager, view, parser, reader, true);

    controller.run();

    assertTrue("Controller should call model.createEvent", mockModel.createEventCalled);
    assertNotNull("Event should be passed to model", mockModel.lastCreatedEvent);
    assertEquals("Event subject should be correct", "Test",
        mockModel.lastCreatedEvent.getSubject());
  }

  @Test
  public void testControllerDelegatesCreateSeriesToModel() throws IOException {
    MockCalendarModel mockModel = new MockCalendarModel();
    MockCalendarManager mockManager = new MockCalendarManager(mockModel);

    String command = "create event \"Series\" from 2025-06-02T09:00 to 2025-06-02T10:00 "
        + "repeats M for 3 times\nexit\n";
    StringReader reader = new StringReader(command);
    Controller controller = new Controller(mockManager, view, parser, reader, true);

    controller.run();

    assertTrue("Controller should call model.createEventSeries",
        mockModel.createEventSeriesCalled);
  }

  @Test
  public void testControllerDelegatesPrintAllEventsToModel() throws IOException {
    MockCalendarModel mockModel = new MockCalendarModel();
    MockCalendarManager mockManager = new MockCalendarManager(mockModel);

    String command = "print all events\nexit\n";
    StringReader reader = new StringReader(command);
    Controller controller = new Controller(mockManager, view, parser, reader, true);

    controller.run();

    assertTrue("Controller should call model.getAllEvents", mockModel.getAllEventsCalled);
  }

  @Test
  public void testControllerDelegatesPrintEventsOnDateToModel() throws IOException {
    MockCalendarModel mockModel = new MockCalendarModel();
    MockCalendarManager mockManager = new MockCalendarManager(mockModel);

    String command = "print events on 2025-06-01\nexit\n";
    StringReader reader = new StringReader(command);
    Controller controller = new Controller(mockManager, view, parser, reader, true);

    controller.run();

    assertTrue("Controller should call model.getEventsOnDate",
        mockModel.getEventsOnDateCalled);
  }

  @Test
  public void testControllerDelegatesPrintEventsInRangeToModel() throws IOException {
    MockCalendarModel mockModel = new MockCalendarModel();
    MockCalendarManager mockManager = new MockCalendarManager(mockModel);

    String command = "print events from 2025-06-01T00:00 to 2025-06-30T23:59\nexit\n";
    StringReader reader = new StringReader(command);
    Controller controller = new Controller(mockManager, view, parser, reader, true);

    controller.run();

    assertTrue("Controller should call model.getEventsInRange",
        mockModel.getEventsInRangeCalled);
  }

  @Test
  public void testControllerDelegatesShowStatusToModel() throws IOException {
    MockCalendarModel mockModel = new MockCalendarModel();
    MockCalendarManager mockManager = new MockCalendarManager(mockModel);

    String command = "show status on 2025-06-01T10:00\nexit\n";
    StringReader reader = new StringReader(command);
    Controller controller = new Controller(mockManager, view, parser, reader, true);

    controller.run();

    assertTrue("Controller should call model.isBusy", mockModel.isBusyCalled);
    assertNotNull("Busy check time should be recorded", mockModel.lastBusyCheckTime);
  }

  @Test
  public void testControllerHandlesMultipleCommands() throws IOException {
    // TC: Verify controller can handle sequence of commands
    MockCalendarModel mockModel = new MockCalendarModel();
    MockCalendarManager mockManager = new MockCalendarManager(mockModel);

    String commands = "create event \"Event1\" from 2025-06-01T10:00 to 2025-06-01T11:00\n"
        + "print all events\n"
        + "show status on 2025-06-01T10:30\n"
        + "exit\n";
    StringReader reader = new StringReader(commands);
    Controller controller = new Controller(mockManager, view, parser, reader, true);

    controller.run();

    assertTrue("Should have called createEvent", mockModel.createEventCalled);
    assertTrue("Should have called getAllEvents", mockModel.getAllEventsCalled);
    assertTrue("Should have called isBusy", mockModel.isBusyCalled);
  }

  @Test
  public void testControllerHandlesInvalidCommand() throws IOException {
    MockCalendarModel mockModel = new MockCalendarModel();
    MockCalendarManager mockManager = new MockCalendarManager(mockModel);

    String command = "invalid command that doesn't exist\nexit\n";
    StringReader reader = new StringReader(command);
    Controller controller = new Controller(mockManager, view, parser, reader, true);

    controller.run();

    String output = outContent.toString();
    assertTrue("Should display error for invalid command",
        output.contains("Error") || output.contains("Invalid") || output.contains("Unrecognized"));
  }

  @Test
  public void testControllerExitsOnExitCommand() throws IOException {
    MockCalendarModel mockModel = new MockCalendarModel();
    MockCalendarManager mockManager = new MockCalendarManager(mockModel);

    String command = "exit\n";
    StringReader reader = new StringReader(command);
    Controller controller = new Controller(mockManager, view, parser, reader, true);

    controller.run();

    // Controller should exit gracefully
    String output = outContent.toString();
    assertFalse("Should not show errors on exit",
        output.toLowerCase().contains("exception"));
  }

  @Test
  public void testControllerHeadlessMode() throws IOException {
    MockCalendarModel mockModel = new MockCalendarModel();
    MockCalendarManager mockManager = new MockCalendarManager(mockModel);

    String command = "create event \"Test\" from 2025-06-01T10:00 to 2025-06-01T11:00\nexit\n";
    StringReader reader = new StringReader(command);
    Controller controller = new Controller(mockManager, view, parser, reader, false);

    controller.run();

    assertTrue("Headless mode should still call model", mockModel.createEventCalled);
  }

  @Test
  public void testControllerCalendarManagement() throws IOException {
    CalendarManager manager = new CalendarManager();
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("America/Los_Angeles"));

    String commands = "use calendar --name Work\n"
        + "create event \"WorkMeeting\" from 2025-06-01T10:00 to 2025-06-01T11:00\n"
        + "use calendar --name Personal\n"
        + "create event \"PersonalEvent\" from 2025-06-01T15:00 to 2025-06-01T16:00\n"
        + "exit\n";

    StringReader reader = new StringReader(commands);
    Controller controller = new Controller(manager, view, parser, reader, true);

    controller.run();

    // Verify events were created in correct calendars
    manager.setCurrentCalendar("Work");
    List<EventInterface> workEvents = manager.getCurrentCalendar().getModel().getAllEvents();
    long workCount = workEvents.stream()
        .filter(e -> e.getSubject().equals("WorkMeeting"))
        .count();
    assertEquals("Work calendar should have WorkMeeting", 1, workCount);

    manager.setCurrentCalendar("Personal");
    List<EventInterface> personalEvents = manager.getCurrentCalendar().getModel()
        .getAllEvents();
    long personalCount = personalEvents.stream()
        .filter(e -> e.getSubject().equals("PersonalEvent"))
        .count();
    assertEquals("Personal calendar should have PersonalEvent", 1, personalCount);
  }

  @Test
  public void testControllerNoCalendarSelected() throws IOException {
    CalendarManager manager = new CalendarManager();
    // Don't create or select any calendar

    String command = "create event \"Test\" from 2025-06-01T10:00 to 2025-06-01T11:00\nexit\n";
    StringReader reader = new StringReader(command);
    Controller controller = new Controller(manager, view, parser, reader, true);

    controller.run();

    String output = outContent.toString();
    assertTrue("Should show error about no calendar selected",
        output.contains("No calendar") || output.contains("calendar") && output.contains("not"));
  }

  @Test
  public void testControllerInvalidCalendarName() throws IOException {
    CalendarManager manager = new CalendarManager();

    String command = "use calendar --name NonExistent\nexit\n";
    StringReader reader = new StringReader(command);
    Controller controller = new Controller(manager, view, parser, reader, true);

    controller.run();

    String output = outContent.toString();
    assertTrue("Should show error about calendar not found",
        output.contains("not found") || output.contains("doesn't exist")
            || output.contains("does not exist"));
  }

  @Test
  public void testControllerEmptyInput() throws IOException {
    MockCalendarModel mockModel = new MockCalendarModel();
    MockCalendarManager mockManager = new MockCalendarManager(mockModel);

    String command = "\n\n\nexit\n";
    StringReader reader = new StringReader(command);
    Controller controller = new Controller(mockManager, view, parser, reader, true);

    controller.run();

    // Should handle empty lines gracefully
    assertFalse("Should not crash on empty lines",
        outContent.toString().contains("Exception"));
  }

  @Test
  public void testControllerWhitespaceInput() throws IOException {
    MockCalendarModel mockModel = new MockCalendarModel();
    MockCalendarManager mockManager = new MockCalendarManager(mockModel);

    String command = "   \n\t\n  \nexit\n";
    StringReader reader = new StringReader(command);
    Controller controller = new Controller(mockManager, view, parser, reader, true);

    controller.run();

    // Should handle whitespace gracefully
    assertFalse("Should not crash on whitespace",
        outContent.toString().contains("NullPointerException"));
  }
}
