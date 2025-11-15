import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import calendar.command.CreateAllDayEventCommand;
import calendar.command.EditEventsCommand;
import calendar.command.EditSeriesCommand;
import calendar.command.ExportCommand;
import calendar.command.PrintAllEventsCommand;
import calendar.command.PrintEventsOnCommand;
import calendar.command.PrintEventsRangeCommand;
import calendar.command.ShowStatusCommand;
import calendar.model.CalendarManager;
import calendar.model.Event;
import calendar.model.EventInterface;
import calendar.model.EventSeries;
import calendar.util.CsvExporter;
import calendar.util.DateTimeParser;
import calendar.utils.TimezoneUtils;
import calendar.view.ConsoleView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

/**
 * Final comprehensive tests to achieve maximum coverage.
 */
public class FinalCoverageTest {

  private CalendarManager manager;
  private ConsoleView view;
  private ByteArrayOutputStream outputStream;

  /**
   * Sets up the test environment before each test.
   */
  @Before
  public void setUp() {
    manager = new CalendarManager();
    outputStream = new ByteArrayOutputStream();
    view = new ConsoleView(new PrintStream(outputStream));
    manager.createCalendar("TestCal", ZoneId.of("America/New_York"));
    manager.setCurrentCalendar("TestCal");
  }

  @Test
  public void testTimezoneUtilsConstructor() {
    try {
      Constructor<TimezoneUtils> constructor = TimezoneUtils.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      assertThrows(InvocationTargetException.class, constructor::newInstance);
    } catch (NoSuchMethodException e) {
      fail("Constructor not found");
    }
  }

  @Test
  public void testEditSeriesCommandNoCalendar() throws IOException {
    CalendarManager freshManager = new CalendarManager();
    EditSeriesCommand cmd = new EditSeriesCommand("subject", "Event", "2025-06-01T10:00", "New");
    assertFalse(cmd.execute(freshManager, view));
  }

  @Test
  public void testEditSeriesCommandEventNotFound() throws IOException {
    EditSeriesCommand cmd = new EditSeriesCommand("subject", "NonExistent",
        "2025-06-01T10:00", "New");
    assertFalse(cmd.execute(manager, view));
  }

  @Test
  public void testEditSeriesCommandAllProperties() throws IOException {
    EventInterface template = new Event("Series",
        LocalDateTime.of(2025, 6, 1, 9, 0),
        LocalDateTime.of(2025, 6, 1, 10, 0),
        null, null, false, UUID.randomUUID(), null);
    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);
    EventSeries series = new EventSeries(seriesId, template, weekdays, null, 3, false);
    manager.getCurrentCalendar().getModel().createEventSeries(series);

    outputStream.reset();
    EditSeriesCommand cmd1 = new EditSeriesCommand("description", "Series",
        "2025-06-02T09:00", "New Desc");
    assertTrue(cmd1.execute(manager, view));

    outputStream.reset();
    EditSeriesCommand cmd2 = new EditSeriesCommand("location", "Series",
        "2025-06-02T09:00", "Room 1");
    assertTrue(cmd2.execute(manager, view));

    outputStream.reset();
    EditSeriesCommand cmd3 = new EditSeriesCommand("status", "Series",
        "2025-06-02T09:00", "private");
    assertTrue(cmd3.execute(manager, view));

    outputStream.reset();
    EditSeriesCommand cmd4 = new EditSeriesCommand("subject", "Series",
        "2025-06-02T09:00", "NewSeries");
    assertTrue(cmd4.execute(manager, view));
  }

  @Test
  public void testEditEventsCommandNoCalendar() throws IOException {
    CalendarManager freshManager = new CalendarManager();
    EditEventsCommand cmd = new EditEventsCommand("subject", "Event", "2025-06-01T10:00", "New");
    assertFalse(cmd.execute(freshManager, view));
  }

  @Test
  public void testEditEventsCommandEventNotFound() throws IOException {
    EditEventsCommand cmd = new EditEventsCommand("subject", "NonExistent",
        "2025-06-01T10:00", "New");
    assertFalse(cmd.execute(manager, view));
  }

  @Test
  public void testEditEventsCommandAllProperties() throws IOException {
    EventInterface template = new Event("Series",
        LocalDateTime.of(2025, 6, 1, 9, 0),
        LocalDateTime.of(2025, 6, 1, 10, 0),
        null, null, false, UUID.randomUUID(), null);
    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);
    EventSeries series = new EventSeries(seriesId, template, weekdays, null, 5, false);
    manager.getCurrentCalendar().getModel().createEventSeries(series);

    outputStream.reset();
    EditEventsCommand cmd1 = new EditEventsCommand("description", "Series",
        "2025-06-04T09:00", "New Desc");
    assertTrue(cmd1.execute(manager, view));

    outputStream.reset();
    EditEventsCommand cmd2 = new EditEventsCommand("location", "Series",
        "2025-06-04T09:00", "Room 1");
    assertTrue(cmd2.execute(manager, view));

    outputStream.reset();
    EditEventsCommand cmd3 = new EditEventsCommand("status", "Series",
        "2025-06-04T09:00", "private");
    assertTrue(cmd3.execute(manager, view));

    outputStream.reset();
    EditEventsCommand cmd4 = new EditEventsCommand("subject", "Series",
        "2025-06-04T09:00", "NewSeries");
    assertTrue(cmd4.execute(manager, view));
  }

  @Test
  public void testPrintAllEventsNoCalendar() throws IOException {
    CalendarManager freshManager = new CalendarManager();
    PrintAllEventsCommand cmd = new PrintAllEventsCommand();
    assertFalse(cmd.execute(freshManager, view));
  }

  @Test
  public void testPrintEventsOnNoCalendar() throws IOException {
    CalendarManager freshManager = new CalendarManager();
    PrintEventsOnCommand cmd = new PrintEventsOnCommand("2025-06-01");
    assertFalse(cmd.execute(freshManager, view));
  }

  @Test
  public void testPrintEventsRangeNoCalendar() throws IOException {
    CalendarManager freshManager = new CalendarManager();
    PrintEventsRangeCommand cmd = new PrintEventsRangeCommand("2025-06-01", "2025-06-05");
    assertFalse(cmd.execute(freshManager, view));
  }

  @Test
  public void testShowStatusNoCalendar() throws IOException {
    CalendarManager freshManager = new CalendarManager();
    ShowStatusCommand cmd = new ShowStatusCommand("2025-06-01T10:00");
    assertFalse(cmd.execute(freshManager, view));
  }

  @Test
  public void testExportCommandNoCalendar() throws IOException {
    CalendarManager freshManager = new CalendarManager();
    ExportCommand cmd = new ExportCommand("test.csv");
    assertFalse(cmd.execute(freshManager, view));
  }

  @Test
  public void testExportCommandIcalFormat() throws IOException {
    Event event = new Event("Test",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, UUID.randomUUID(), null);
    manager.getCurrentCalendar().getModel().createEvent(event);

    Path tempFile = Files.createTempFile("test-export", ".ical");
    try {
      ExportCommand cmd = new ExportCommand(tempFile.toString());
      assertTrue(cmd.execute(manager, view));
      assertTrue(Files.exists(tempFile));
    } finally {
      Files.deleteIfExists(tempFile);
    }
  }

  @Test
  public void testCreateAllDayEventCommandNoCalendar() throws IOException {
    CalendarManager freshManager = new CalendarManager();
    CreateAllDayEventCommand cmd = new CreateAllDayEventCommand("Event", "2025-06-01");
    assertFalse(cmd.execute(freshManager, view));
  }

  @Test
  public void testCsvExporterEmptyList() {
    List<EventInterface> events = new java.util.ArrayList<>();
    String csv = CsvExporter.toCsv(events);
    assertNotNull(csv);
    assertTrue(csv.contains("Subject"));
  }

  @Test
  public void testCsvExporterWithEvents() {
    Event event = new Event("Test Event",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        "Description",
        "Location",
        true,
        UUID.randomUUID(),
        null);
    List<EventInterface> events = new java.util.ArrayList<>();
    events.add(event);

    String csv = CsvExporter.toCsv(events);
    assertNotNull(csv);
    assertTrue(csv.contains("Test Event"));
  }

  @Test
  public void testDateTimeParserValidFormats() {
    assertNotNull(DateTimeParser.parseDateTime("2025-06-01T10:00"));
    assertNotNull(DateTimeParser.parseDate("2025-06-01"));
  }
}
