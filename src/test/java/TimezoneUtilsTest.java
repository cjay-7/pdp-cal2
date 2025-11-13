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
    // 2:00 PM EST should become 11:00 AM PST (same moment in time)
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
    // 11:00 AM PST should become 2:00 PM EST
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
    // 9:00 AM Tokyo (June 2) should become 8:00 PM New York (June 1, previous day)
    LocalDateTime tokyo = LocalDateTime.of(2025, 6, 2, 9, 0);
    ZoneId fromZone = ZoneId.of("Asia/Tokyo");
    ZoneId toZone = ZoneId.of("America/New_York");

    LocalDateTime result = TimezoneUtils.convertTimezone(tokyo, fromZone, toZone);

    assertEquals("Hour should be 8 PM", 20, result.getHour());
    assertEquals("Day should be June 1", 1, result.getDayOfMonth());
  }

  @Test
  public void testConvertSameTimezone() {
    // Converting within same timezone should return same time
    LocalDateTime time = LocalDateTime.of(2025, 6, 1, 10, 30);
    ZoneId zone = ZoneId.of("America/New_York");

    LocalDateTime result = TimezoneUtils.convertTimezone(time, zone, zone);

    assertEquals("Time should be unchanged", time, result);
  }

  @Test
  public void testConvertWithMinutes() {
    // Test that minutes are preserved
    LocalDateTime est = LocalDateTime.of(2025, 6, 1, 14, 45);
    ZoneId fromZone = ZoneId.of("America/New_York");
    ZoneId toZone = ZoneId.of("America/Los_Angeles");

    LocalDateTime result = TimezoneUtils.convertTimezone(est, fromZone, toZone);

    assertEquals("Minute should be preserved", 45, result.getMinute());
  }

  @Test
  public void testConvertParisToNewYork() {
    // 3:00 PM Paris should become 9:00 AM New York (6 hour difference in summer)
    LocalDateTime paris = LocalDateTime.of(2025, 6, 1, 15, 0);
    ZoneId fromZone = ZoneId.of("Europe/Paris");
    ZoneId toZone = ZoneId.of("America/New_York");

    LocalDateTime result = TimezoneUtils.convertTimezone(paris, fromZone, toZone);

    assertEquals("Hour should be 9 AM", 9, result.getHour());
  }

  @Test
  public void testConvertDuringDstTransition() {
    // Test conversion during daylight saving time
    LocalDateTime time = LocalDateTime.of(2025, 3, 10, 14, 0);
    ZoneId fromZone = ZoneId.of("America/New_York");
    ZoneId toZone = ZoneId.of("America/Denver");

    LocalDateTime result = TimezoneUtils.convertTimezone(time, fromZone, toZone);

    // Result should be 2 hours behind (Mountain time)
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
