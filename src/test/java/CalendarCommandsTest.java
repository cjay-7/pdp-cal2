import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import calendar.command.CreateCalendarCommand;
import calendar.command.EditCalendarCommand;
import calendar.command.UseCalendarCommand;
import calendar.model.CalendarManager;
import calendar.view.ConsoleView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.ZoneId;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for calendar commands: CreateCalendar, UseCalendar, EditCalendar.
 */
public class CalendarCommandsTest {

  private CalendarManager manager;
  private ByteArrayOutputStream outputStream;
  private ConsoleView view;

  /**
   * Sets up the test environment before each test.
   */
  @Before
  public void setUp() {
    manager = new CalendarManager();
    outputStream = new ByteArrayOutputStream();
    view = new ConsoleView(new PrintStream(outputStream));
  }

  @Test
  public void testCreateCalendarSuccess() throws IOException {
    CreateCalendarCommand command = new CreateCalendarCommand("WorkCal", "America/New_York");
    boolean result = command.execute(manager, view);

    assertTrue(result);
    String output = outputStream.toString();
    assertTrue(output.contains("Calendar 'WorkCal' created successfully"));
    assertTrue(output.contains("America/New_York"));
  }

  @Test
  public void testCreateCalendarDuplicateName() throws IOException {
    manager.createCalendar("WorkCal", ZoneId.of("America/New_York"));

    CreateCalendarCommand command = new CreateCalendarCommand("WorkCal", "America/Los_Angeles");
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("already exists"));
  }

  @Test
  public void testCreateCalendarInvalidTimezone() throws IOException {
    CreateCalendarCommand command = new CreateCalendarCommand("TestCal", "Invalid/Timezone");
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("Invalid timezone"));
    assertTrue(output.contains("IANA format"));
  }

  @Test
  public void testCreateCalendarVariousTimezones() throws IOException {
    String[] timezones = {"UTC", "Europe/London", "Asia/Tokyo", "Australia/Sydney"};
    for (int i = 0; i < timezones.length; i++) {
      outputStream.reset();
      CreateCalendarCommand command = new CreateCalendarCommand("Cal" + i, timezones[i]);
      assertTrue(command.execute(manager, view));
    }
  }

  @Test
  public void testUseCalendarSuccess() throws IOException {
    manager.createCalendar("TestCal", ZoneId.of("America/New_York"));

    UseCalendarCommand command = new UseCalendarCommand("TestCal");
    boolean result = command.execute(manager, view);

    assertTrue(result);
    String output = outputStream.toString();
    assertTrue(output.contains("Now using calendar 'TestCal'"));
    assertEquals("TestCal", manager.getCurrentCalendar().getName());
  }

  @Test
  public void testUseCalendarNotFound() throws IOException {
    UseCalendarCommand command = new UseCalendarCommand("NonExistent");
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("not found"));
  }

  @Test
  public void testUseCalendarSwitchBetweenCalendars() throws IOException {
    manager.createCalendar("Cal1", ZoneId.of("UTC"));
    manager.createCalendar("Cal2", ZoneId.of("America/New_York"));

    UseCalendarCommand command1 = new UseCalendarCommand("Cal1");
    assertTrue(command1.execute(manager, view));
    assertEquals("Cal1", manager.getCurrentCalendar().getName());

    outputStream.reset();
    UseCalendarCommand command2 = new UseCalendarCommand("Cal2");
    assertTrue(command2.execute(manager, view));
    assertEquals("Cal2", manager.getCurrentCalendar().getName());
  }

  @Test
  public void testEditCalendarNameSuccess() throws IOException {
    manager.createCalendar("OldName", ZoneId.of("UTC"));

    EditCalendarCommand command = new EditCalendarCommand("OldName", "name", "NewName");
    boolean result = command.execute(manager, view);

    assertTrue(result);
    String output = outputStream.toString();
    assertTrue(output.contains("Calendar name changed"));
    assertTrue(output.contains("OldName"));
    assertTrue(output.contains("NewName"));
    assertNotNull(manager.getCalendar("NewName"));
    assertNull(manager.getCalendar("OldName"));
  }

  @Test
  public void testEditCalendarNameNotFound() throws IOException {
    EditCalendarCommand command = new EditCalendarCommand("NonExistent", "name", "NewName");
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("not found") || output.contains("already exists"));
  }

  @Test
  public void testEditCalendarNameAlreadyExists() throws IOException {
    manager.createCalendar("Cal1", ZoneId.of("UTC"));
    manager.createCalendar("Cal2", ZoneId.of("UTC"));

    EditCalendarCommand command = new EditCalendarCommand("Cal1", "name", "Cal2");
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("not found") || output.contains("already exists"));
  }

  @Test
  public void testEditCalendarTimezoneSuccess() throws IOException {
    manager.createCalendar("TestCal", ZoneId.of("UTC"));

    EditCalendarCommand command = new EditCalendarCommand("TestCal", "timezone",
        "America/New_York");
    boolean result = command.execute(manager, view);

    assertTrue(result);
    String output = outputStream.toString();
    assertTrue(output.contains("timezone changed"));
    assertTrue(output.contains("America/New_York"));
    assertEquals(ZoneId.of("America/New_York"), manager.getCalendar("TestCal").getTimezone());
  }

  @Test
  public void testEditCalendarTimezoneInvalid() throws IOException {
    manager.createCalendar("TestCal", ZoneId.of("UTC"));

    EditCalendarCommand command = new EditCalendarCommand("TestCal", "timezone", "Invalid/TZ");
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("Invalid timezone"));
    assertTrue(output.contains("IANA format"));
  }

  @Test
  public void testEditCalendarTimezoneNotFound() throws IOException {
    EditCalendarCommand command = new EditCalendarCommand("NonExistent", "timezone", "UTC");
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("not found"));
  }

  @Test
  public void testEditCalendarUnknownProperty() throws IOException {
    manager.createCalendar("TestCal", ZoneId.of("UTC"));

    EditCalendarCommand command = new EditCalendarCommand("TestCal", "color", "blue");
    boolean result = command.execute(manager, view);

    assertFalse(result);
    String output = outputStream.toString();
    assertTrue(output.contains("Unknown property"));
    assertTrue(output.contains("name"));
    assertTrue(output.contains("timezone"));
  }

  @Test
  public void testEditCalendarPropertyCaseInsensitive() throws IOException {
    manager.createCalendar("TestCal", ZoneId.of("UTC"));

    EditCalendarCommand command1 = new EditCalendarCommand("TestCal", "NAME", "NewName");
    assertTrue(command1.execute(manager, view));

    outputStream.reset();
    EditCalendarCommand command2 = new EditCalendarCommand("NewName", "TIMEZONE", "Europe/London");
    assertTrue(command2.execute(manager, view));
  }
}
