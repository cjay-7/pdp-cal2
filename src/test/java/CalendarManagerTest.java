import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import java.time.ZoneId;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for CalendarManager.
 * Tests calendar management, uniqueness constraints, and current calendar tracking.
 */
public class CalendarManagerTest {
  private CalendarManager manager;

  /**
   * Sets up test fixtures.
   */
  @Before
  public void setUp() {
    manager = new CalendarManager();
  }

  @Test
  public void testCreateCalendarSuccess() {
    boolean result = manager.createCalendar("Work", ZoneId.of("America/New_York"));

    assertTrue("Calendar should be created successfully", result);
    assertNotNull("Calendar should exist", manager.getCalendar("Work"));
    assertEquals("Calendar name should be 'Work'", "Work",
        manager.getCalendar("Work").getName());
  }

  @Test
  public void testCreateMultipleCalendars() {
    assertTrue(manager.createCalendar("Work", ZoneId.of("America/New_York")));
    assertTrue(manager.createCalendar("Personal", ZoneId.of("Europe/Paris")));
    assertTrue(manager.createCalendar("Family", ZoneId.of("Asia/Tokyo")));

    assertEquals("Should have 3 calendars", 3, manager.getAllCalendars().size());
    assertNotNull("Work calendar should exist", manager.getCalendar("Work"));
    assertNotNull("Personal calendar should exist", manager.getCalendar("Personal"));
    assertNotNull("Family calendar should exist", manager.getCalendar("Family"));
  }

  @Test
  public void testCreateCalendarWithDuplicateName() {
    assertTrue(manager.createCalendar("Work", ZoneId.of("America/New_York")));
    assertFalse("Should not create calendar with duplicate name",
        manager.createCalendar("Work", ZoneId.of("Europe/Paris")));

    assertEquals("Should still have only 1 calendar", 1, manager.getAllCalendars().size());
  }

  @Test
  public void testCreateCalendarCaseInsensitive() {
    assertTrue(manager.createCalendar("Work", ZoneId.of("America/New_York")));
    assertFalse("Should not create calendar with same name (different case)",
        manager.createCalendar("work", ZoneId.of("Europe/Paris")));
  }

  @Test
  public void testGetCalendarByName() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));

    Calendar cal = manager.getCalendar("Work");
    assertNotNull("Calendar should be found", cal);
    assertEquals("Name should be 'Work'", "Work", cal.getName());
    assertEquals("Timezone should be America/New_York",
        ZoneId.of("America/New_York"), cal.getTimezone());
  }

  @Test
  public void testGetNonExistentCalendar() {
    assertNull("Should return null for non-existent calendar",
        manager.getCalendar("NonExistent"));
  }

  @Test
  public void testSetCurrentCalendar() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));

    boolean result = manager.setCurrentCalendar("Work");

    assertTrue("Should set current calendar successfully", result);
    assertEquals("Current calendar should be 'Work'", "Work",
        manager.getCurrentCalendar().getName());
  }

  @Test
  public void testSetCurrentCalendarToNonExistent() {
    assertFalse("Should fail to set non-existent calendar as current",
        manager.setCurrentCalendar("NonExistent"));
    assertNull("Current calendar should remain null", manager.getCurrentCalendar());
  }

  @Test
  public void testGetCurrentCalendarWhenNone() {
    assertNull("Current calendar should be null when none set",
        manager.getCurrentCalendar());
  }

  @Test
  public void testSwitchCurrentCalendar() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("Europe/Paris"));

    manager.setCurrentCalendar("Work");
    assertEquals("Current should be Work", "Work",
        manager.getCurrentCalendar().getName());

    manager.setCurrentCalendar("Personal");
    assertEquals("Current should be Personal", "Personal",
        manager.getCurrentCalendar().getName());
  }

  @Test
  public void testEditCalendarName() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.setCurrentCalendar("Work");

    boolean result = manager.editCalendarName("Work", "WorkCal");

    assertTrue("Should edit name successfully", result);
    assertNotNull("Calendar should exist with new name", manager.getCalendar("WorkCal"));
    assertNull("Old name should not exist", manager.getCalendar("Work"));
    assertEquals("Current calendar name should be updated", "WorkCal",
        manager.getCurrentCalendar().getName());
  }

  @Test
  public void testEditCalendarNameToExistingName() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("Europe/Paris"));

    boolean result = manager.editCalendarName("Work", "Personal");

    assertFalse("Should not edit to existing name", result);
    assertNotNull("Work should still exist", manager.getCalendar("Work"));
  }

  @Test
  public void testEditCalendarNameNonExistent() {
    assertFalse("Should fail to edit non-existent calendar",
        manager.editCalendarName("NonExistent", "NewName"));
  }

  @Test
  public void testEditCalendarTimezone() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));

    boolean result = manager.editCalendarTimezone("Work", ZoneId.of("Europe/Paris"));

    assertTrue("Should edit timezone successfully", result);
    assertEquals("Timezone should be updated", ZoneId.of("Europe/Paris"),
        manager.getCalendar("Work").getTimezone());
  }

  @Test
  public void testEditCalendarTimezoneNonExistent() {
    assertFalse("Should fail to edit non-existent calendar",
        manager.editCalendarTimezone("NonExistent", ZoneId.of("America/New_York")));
  }

  @Test
  public void testGetAllCalendars() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("Europe/Paris"));
    manager.createCalendar("Family", ZoneId.of("Asia/Tokyo"));

    assertEquals("Should have 3 calendars", 3, manager.getAllCalendars().size());
  }

  @Test
  public void testGetAllCalendarsWhenEmpty() {
    assertTrue("Should have empty list when no calendars",
        manager.getAllCalendars().isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarWithNullName() {
    manager.createCalendar(null, ZoneId.of("America/New_York"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarWithEmptyName() {
    manager.createCalendar("", ZoneId.of("America/New_York"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarWithNullTimezone() {
    manager.createCalendar("Work", null);
  }

  @Test
  public void testCalendarNamePreservesWhitespace() {
    assertTrue(manager.createCalendar("My Work Calendar", ZoneId.of("America/New_York")));

    Calendar cal = manager.getCalendar("My Work Calendar");
    assertNotNull("Calendar with spaces should be found", cal);
    assertEquals("Name should preserve spaces", "My Work Calendar", cal.getName());
  }
}
