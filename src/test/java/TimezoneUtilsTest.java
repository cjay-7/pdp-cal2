import static org.junit.Assert.assertEquals;

import calendar.utils.TimezoneUtils;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.Test;

/**
 * Test class for TimezoneUtils.
 * Tests timezone conversions following Interpretation B (preserve absolute time).
 */
public class TimezoneUtilsTest {

  @Test
  public void testConvertEstToPst() {
    
    LocalDateTime est = LocalDateTime.of(2025, 6, 1, 14, 0);
    ZoneId fromZone = ZoneId.of("America/New_York");
    ZoneId toZone = ZoneId.of("America/Los_Angeles");

    LocalDateTime result = TimezoneUtils.convertTimezone(est, fromZone, toZone);

    assertEquals("Hour should be 11 AM", 11, result.getHour());
    assertEquals("Minute should be 0", 0, result.getMinute());
    assertEquals("Date should be same", est.toLocalDate(), result.toLocalDate());
  }

  @Test
  public void testConvertPstToEst() {
    
    LocalDateTime pst = LocalDateTime.of(2025, 6, 1, 11, 0);
    ZoneId fromZone = ZoneId.of("America/Los_Angeles");
    ZoneId toZone = ZoneId.of("America/New_York");

    LocalDateTime result = TimezoneUtils.convertTimezone(pst, fromZone, toZone);

    assertEquals("Hour should be 2 PM", 14, result.getHour());
    assertEquals("Minute should be 0", 0, result.getMinute());
    assertEquals("Date should be same", pst.toLocalDate(), result.toLocalDate());
  }

  @Test
  public void testConvertTokyoToNewYork() {
    
    LocalDateTime tokyo = LocalDateTime.of(2025, 6, 2, 9, 0);
    ZoneId fromZone = ZoneId.of("Asia/Tokyo");
    ZoneId toZone = ZoneId.of("America/New_York");

    LocalDateTime result = TimezoneUtils.convertTimezone(tokyo, fromZone, toZone);

    assertEquals("Hour should be 8 PM", 20, result.getHour());
    assertEquals("Day should be June 1", 1, result.getDayOfMonth());
  }

  @Test
  public void testConvertSameTimezone() {
    
    LocalDateTime time = LocalDateTime.of(2025, 6, 1, 10, 30);
    ZoneId zone = ZoneId.of("America/New_York");

    LocalDateTime result = TimezoneUtils.convertTimezone(time, zone, zone);

    assertEquals("Time should be unchanged", time, result);
  }

  @Test
  public void testConvertWithMinutes() {
    
    LocalDateTime est = LocalDateTime.of(2025, 6, 1, 14, 45);
    ZoneId fromZone = ZoneId.of("America/New_York");
    ZoneId toZone = ZoneId.of("America/Los_Angeles");

    LocalDateTime result = TimezoneUtils.convertTimezone(est, fromZone, toZone);

    assertEquals("Minute should be preserved", 45, result.getMinute());
  }

  @Test
  public void testConvertParisToNewYork() {
    
    LocalDateTime paris = LocalDateTime.of(2025, 6, 1, 15, 0);
    ZoneId fromZone = ZoneId.of("Europe/Paris");
    ZoneId toZone = ZoneId.of("America/New_York");

    LocalDateTime result = TimezoneUtils.convertTimezone(paris, fromZone, toZone);

    assertEquals("Hour should be 9 AM", 9, result.getHour());
  }

  @Test
  public void testConvertDuringDstTransition() {
    
    LocalDateTime time = LocalDateTime.of(2025, 3, 10, 14, 0);
    ZoneId fromZone = ZoneId.of("America/New_York");
    ZoneId toZone = ZoneId.of("America/Denver");

    LocalDateTime result = TimezoneUtils.convertTimezone(time, fromZone, toZone);

    
    assertEquals("Hour should be 12 PM", 12, result.getHour());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConvertWithNullDateTime() {
    TimezoneUtils.convertTimezone(null, ZoneId.of("America/New_York"),
        ZoneId.of("America/Los_Angeles"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConvertWithNullFromZone() {
    TimezoneUtils.convertTimezone(LocalDateTime.of(2025, 6, 1, 14, 0),
        null, ZoneId.of("America/Los_Angeles"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConvertWithNullToZone() {
    TimezoneUtils.convertTimezone(LocalDateTime.of(2025, 6, 1, 14, 0),
        ZoneId.of("America/New_York"), null);
  }
}
