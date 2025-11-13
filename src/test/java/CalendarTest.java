import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import calendar.model.Calendar;
import calendar.model.CalendarModel;
import calendar.model.CalendarModelInterface;
import java.time.ZoneId;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for Calendar.
 * Tests calendar creation, property management, and timezone handling.
 */
public class CalendarTest {
  private CalendarModelInterface model;

  /**
   * Sets up test fixtures.
   */
  @Before
  public void setUp() {
    model = new CalendarModel();
  }

  @Test
  public void testCreateCalendarWithValidData() {
    Calendar cal = new Calendar("Work", ZoneId.of("America/New_York"), model);

    assertNotNull("Calendar should not be null", cal);
    assertEquals("Calendar name should be 'Work'", "Work", cal.getName());
    assertEquals("Timezone should be America/New_York",
        ZoneId.of("America/New_York"), cal.getTimezone());
    assertNotNull("Calendar model should not be null", cal.getModel());
  }

  @Test
  public void testSetCalendarName() {
    Calendar cal = new Calendar("Work", ZoneId.of("America/New_York"), model);
    cal.setName("Personal");

    assertEquals("Calendar name should be updated to 'Personal'", "Personal", cal.getName());
  }

  @Test
  public void testSetCalendarTimezone() {
    Calendar cal = new Calendar("Work", ZoneId.of("America/New_York"), model);
    cal.setTimezone(ZoneId.of("Europe/Paris"));

    assertEquals("Timezone should be updated to Europe/Paris",
        ZoneId.of("Europe/Paris"), cal.getTimezone());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarWithNullName() {
    new Calendar(null, ZoneId.of("America/New_York"), model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarWithEmptyName() {
    new Calendar("", ZoneId.of("America/New_York"), model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarWithNullTimezone() {
    new Calendar("Work", null, model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarWithNullModel() {
    new Calendar("Work", ZoneId.of("America/New_York"), null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNameToNull() {
    Calendar cal = new Calendar("Work", ZoneId.of("America/New_York"), model);
    cal.setName(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNameToEmpty() {
    Calendar cal = new Calendar("Work", ZoneId.of("America/New_York"), model);
    cal.setName("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetTimezoneToNull() {
    Calendar cal = new Calendar("Work", ZoneId.of("America/New_York"), model);
    cal.setTimezone(null);
  }

  @Test
  public void testMultipleTimezones() {
    Calendar cal1 = new Calendar("NYC", ZoneId.of("America/New_York"), model);
    Calendar cal2 = new Calendar("Paris", ZoneId.of("Europe/Paris"), new CalendarModel());
    Calendar cal3 = new Calendar("Tokyo", ZoneId.of("Asia/Tokyo"), new CalendarModel());

    assertEquals("NYC should be in America/New_York",
        ZoneId.of("America/New_York"), cal1.getTimezone());
    assertEquals("Paris should be in Europe/Paris",
        ZoneId.of("Europe/Paris"), cal2.getTimezone());
    assertEquals("Tokyo should be in Asia/Tokyo",
        ZoneId.of("Asia/Tokyo"), cal3.getTimezone());
  }

  @Test
  public void testCalendarWithWhitespaceName() {
    Calendar cal = new Calendar("My Work Calendar", ZoneId.of("America/New_York"), model);
    assertEquals("Calendar name with spaces should be preserved",
        "My Work Calendar", cal.getName());
  }
}
