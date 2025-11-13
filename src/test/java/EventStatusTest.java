import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import calendar.model.EventStatus;
import org.junit.Test;

/**
 * Tests for EventStatus enum.
 */
public class EventStatusTest {

  @Test
  public void testEnumValues() {
    assertEquals(2, EventStatus.values().length);
    assertEquals(EventStatus.PUBLIC, EventStatus.valueOf("PUBLIC"));
    assertEquals(EventStatus.PRIVATE, EventStatus.valueOf("PRIVATE"));
  }

  @Test
  public void testFromStringPublic() {
    assertEquals(EventStatus.PUBLIC, EventStatus.fromString("public"));
    assertEquals(EventStatus.PUBLIC, EventStatus.fromString("PUBLIC"));
    assertEquals(EventStatus.PUBLIC, EventStatus.fromString("Public"));
    assertEquals(EventStatus.PUBLIC, EventStatus.fromString("  public  "));
  }

  @Test
  public void testFromStringPrivate() {
    assertEquals(EventStatus.PRIVATE, EventStatus.fromString("private"));
    assertEquals(EventStatus.PRIVATE, EventStatus.fromString("PRIVATE"));
    assertEquals(EventStatus.PRIVATE, EventStatus.fromString("Private"));
    assertEquals(EventStatus.PRIVATE, EventStatus.fromString("  private  "));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFromStringNull() {
    EventStatus.fromString(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFromStringInvalid() {
    EventStatus.fromString("invalid");
  }

  @Test
  public void testGetValue() {
    assertEquals("public", EventStatus.PUBLIC.getValue());
    assertEquals("private", EventStatus.PRIVATE.getValue());
  }

  @Test
  public void testToString() {
    assertEquals("public", EventStatus.PUBLIC.toString());
    assertEquals("private", EventStatus.PRIVATE.toString());
  }

  @Test
  public void testIsPrivate() {
    assertFalse(EventStatus.PUBLIC.isPrivate());
    assertTrue(EventStatus.PRIVATE.isPrivate());
  }
}

