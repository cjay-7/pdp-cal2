import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import calendar.DummyCalendar;
import org.junit.Test;

/**
 * Test class for DummyCalendar.
 */
public class DummyCalendarTest {

  @Test
  public void testGetName() {
    DummyCalendar calendar = new DummyCalendar();
    assertEquals("DummyCalendar", calendar.getName());
  }
}
