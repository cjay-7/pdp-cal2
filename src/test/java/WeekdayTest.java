import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import calendar.model.Weekday;
import java.time.DayOfWeek;
import java.util.Set;
import org.junit.Test;

/**
 * Tests for Weekday enum.
 */
public class WeekdayTest {

  @Test
  public void testAllWeekdayValues() {
    assertEquals(7, Weekday.values().length);
    assertEquals(Weekday.MONDAY, Weekday.valueOf("MONDAY"));
    assertEquals(Weekday.TUESDAY, Weekday.valueOf("TUESDAY"));
    assertEquals(Weekday.WEDNESDAY, Weekday.valueOf("WEDNESDAY"));
    assertEquals(Weekday.THURSDAY, Weekday.valueOf("THURSDAY"));
    assertEquals(Weekday.FRIDAY, Weekday.valueOf("FRIDAY"));
    assertEquals(Weekday.SATURDAY, Weekday.valueOf("SATURDAY"));
    assertEquals(Weekday.SUNDAY, Weekday.valueOf("SUNDAY"));
  }

  @Test
  public void testGetAbbreviation() {
    assertEquals('M', Weekday.MONDAY.getAbbreviation());
    assertEquals('T', Weekday.TUESDAY.getAbbreviation());
    assertEquals('W', Weekday.WEDNESDAY.getAbbreviation());
    assertEquals('R', Weekday.THURSDAY.getAbbreviation());
    assertEquals('F', Weekday.FRIDAY.getAbbreviation());
    assertEquals('S', Weekday.SATURDAY.getAbbreviation());
    assertEquals('U', Weekday.SUNDAY.getAbbreviation());
  }

  @Test
  public void testGetDayOfWeek() {
    assertEquals(DayOfWeek.MONDAY, Weekday.MONDAY.getDayOfWeek());
    assertEquals(DayOfWeek.TUESDAY, Weekday.TUESDAY.getDayOfWeek());
    assertEquals(DayOfWeek.WEDNESDAY, Weekday.WEDNESDAY.getDayOfWeek());
    assertEquals(DayOfWeek.THURSDAY, Weekday.THURSDAY.getDayOfWeek());
    assertEquals(DayOfWeek.FRIDAY, Weekday.FRIDAY.getDayOfWeek());
    assertEquals(DayOfWeek.SATURDAY, Weekday.SATURDAY.getDayOfWeek());
    assertEquals(DayOfWeek.SUNDAY, Weekday.SUNDAY.getDayOfWeek());
  }

  @Test
  public void testFromChar() {
    assertEquals(Weekday.MONDAY, Weekday.fromChar('M'));
    assertEquals(Weekday.MONDAY, Weekday.fromChar('m'));
    assertEquals(Weekday.TUESDAY, Weekday.fromChar('T'));
    assertEquals(Weekday.TUESDAY, Weekday.fromChar('t'));
    assertEquals(Weekday.WEDNESDAY, Weekday.fromChar('W'));
    assertEquals(Weekday.THURSDAY, Weekday.fromChar('R'));
    assertEquals(Weekday.THURSDAY, Weekday.fromChar('r'));
    assertEquals(Weekday.FRIDAY, Weekday.fromChar('F'));
    assertEquals(Weekday.SATURDAY, Weekday.fromChar('S'));
    assertEquals(Weekday.SUNDAY, Weekday.fromChar('U'));
  }

  @Test(expected = IllegalArgumentException.class)
  
  
  public void testFromCharInvalid() {
    Weekday.fromChar('X');
  }

  @Test
  public void testParseString() {
    Weekday[] result = Weekday.parseString("MWF");
    assertEquals(3, result.length);
    assertEquals(Weekday.MONDAY, result[0]);
    assertEquals(Weekday.WEDNESDAY, result[1]);
    assertEquals(Weekday.FRIDAY, result[2]);
  }

  @Test
  public void testParseStringAllDays() {
    Weekday[] result = Weekday.parseString("MTWRFSU");
    assertEquals(7, result.length);
  }

  @Test
  public void testParseStringCaseInsensitive() {
    Weekday[] result = Weekday.parseString("mwt");
    assertEquals(3, result.length);
    assertEquals(Weekday.MONDAY, result[0]);
    assertEquals(Weekday.WEDNESDAY, result[1]);
    assertEquals(Weekday.TUESDAY, result[2]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseStringNull() {
    Weekday.parseString(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseStringEmpty() {
    Weekday.parseString("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseStringWhitespace() {
    Weekday.parseString("   ");
  }

  @Test
  public void testFromDayOfWeek() {
    assertEquals(Weekday.MONDAY, Weekday.fromDayOfWeek(DayOfWeek.MONDAY));
    assertEquals(Weekday.TUESDAY, Weekday.fromDayOfWeek(DayOfWeek.TUESDAY));
    assertEquals(Weekday.WEDNESDAY, Weekday.fromDayOfWeek(DayOfWeek.WEDNESDAY));
    assertEquals(Weekday.THURSDAY, Weekday.fromDayOfWeek(DayOfWeek.THURSDAY));
    assertEquals(Weekday.FRIDAY, Weekday.fromDayOfWeek(DayOfWeek.FRIDAY));
    assertEquals(Weekday.SATURDAY, Weekday.fromDayOfWeek(DayOfWeek.SATURDAY));
    assertEquals(Weekday.SUNDAY, Weekday.fromDayOfWeek(DayOfWeek.SUNDAY));
  }

  @Test
  public void testToDayOfWeekSet() {
    Weekday[] weekdays = {Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.FRIDAY};
    Set<DayOfWeek> result = Weekday.toDayOfWeekSet(weekdays);

    assertEquals(3, result.size());
    assertTrue(result.contains(DayOfWeek.MONDAY));
    assertTrue(result.contains(DayOfWeek.WEDNESDAY));
    assertTrue(result.contains(DayOfWeek.FRIDAY));
  }

  @Test
  public void testToString() {
    assertEquals("M", Weekday.MONDAY.toString());
    assertEquals("T", Weekday.TUESDAY.toString());
    assertEquals("W", Weekday.WEDNESDAY.toString());
    assertEquals("R", Weekday.THURSDAY.toString());
    assertEquals("F", Weekday.FRIDAY.toString());
    assertEquals("S", Weekday.SATURDAY.toString());
    assertEquals("U", Weekday.SUNDAY.toString());
  }
}

