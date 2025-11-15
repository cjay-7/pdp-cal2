package calendar.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Utility class for timezone conversions.
 * Implements Interpretation B: preserve absolute time when converting between timezones.
 *
 * <p>DESIGN RATIONALE:
 * - Uses ZonedDateTime for accurate timezone conversions
 * - Follows "Interpretation B" from requirements: preserve absolute time
 * - Example: 2:00 PM EST → 11:00 AM PST (same moment, different local time)
 * - Handles daylight saving time (DST) transitions automatically
 * - Static utility methods for easy use throughout application
 *
 * <p>This approach ensures that when copying events between calendars in different
 * timezones, the absolute moment in time is preserved, which is the standard behavior
 * users expect from calendar applications.
 */
public class TimezoneUtils {

  /**
   * Private constructor to prevent instantiation of utility class.
   */
  private TimezoneUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  /**
   * Converts a LocalDateTime from one timezone to another, preserving absolute time.
   * This follows "Interpretation B" where the same moment in time is preserved.
   *
   * <p>Example: 2:00 PM EST → 11:00 AM PST (same moment, 3 hour difference)
   *
   * @param dateTime the local date-time in the source timezone
   * @param fromZone the source timezone
   * @param toZone the target timezone
   * @return the equivalent local date-time in the target timezone
   * @throws IllegalArgumentException if any parameter is null
   */
  public static LocalDateTime convertTimezone(LocalDateTime dateTime,
                                                ZoneId fromZone,
                                                ZoneId toZone) {
    if (dateTime == null) {
      throw new IllegalArgumentException("DateTime cannot be null");
    }
    if (fromZone == null) {
      throw new IllegalArgumentException("Source timezone cannot be null");
    }
    if (toZone == null) {
      throw new IllegalArgumentException("Target timezone cannot be null");
    }

    if (fromZone.equals(toZone)) {
      return dateTime;
    }

    ZonedDateTime sourceZoned = dateTime.atZone(fromZone);

    ZonedDateTime targetZoned = sourceZoned.withZoneSameInstant(toZone);

    return targetZoned.toLocalDateTime();
  }
}
