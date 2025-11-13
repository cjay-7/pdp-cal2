package calendar.model;

import java.time.ZoneId;
import java.util.Objects;

/**
 * Represents a calendar with a unique name, timezone, and associated calendar model.
 * Each calendar manages its own set of events through its CalendarModel.
 *
 * <p>DESIGN RATIONALE:
 * - Calendar acts as a container that associates a name and timezone with a CalendarModel
 * - This separation allows multiple calendars to exist independently
 * - ZoneId from java.time provides robust timezone handling with IANA database support
 * - Validation ensures calendar integrity (non-null, non-empty names)
 *
 * <p>REPRESENTATION INVARIANTS:
 * - name must not be null or empty
 * - timezone must not be null
 * - model must not be null
 */
public class Calendar {
  private String name;
  private ZoneId timezone;
  private final CalendarModelInterface model;

  /**
   * Creates a new Calendar with the specified name, timezone, and model.
   *
   * @param name the unique name for this calendar
   * @param timezone the timezone for this calendar (IANA format)
   * @param model the calendar model that manages events
   * @throws IllegalArgumentException if name is null/empty, or timezone/model is null
   */
  public Calendar(String name, ZoneId timezone, CalendarModelInterface model) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Calendar name cannot be null or empty");
    }
    if (timezone == null) {
      throw new IllegalArgumentException("Calendar timezone cannot be null");
    }
    if (model == null) {
      throw new IllegalArgumentException("Calendar model cannot be null");
    }

    this.name = name;
    this.timezone = timezone;
    this.model = model;
  }

  /**
   * Gets the name of this calendar.
   *
   * @return the calendar name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this calendar.
   *
   * @param name the new name for this calendar
   * @throws IllegalArgumentException if name is null or empty
   */
  public void setName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Calendar name cannot be null or empty");
    }
    this.name = name;
  }

  /**
   * Gets the timezone of this calendar.
   *
   * @return the calendar timezone
   */
  public ZoneId getTimezone() {
    return timezone;
  }

  /**
   * Sets the timezone of this calendar.
   *
   * @param timezone the new timezone for this calendar
   * @throws IllegalArgumentException if timezone is null
   */
  public void setTimezone(ZoneId timezone) {
    if (timezone == null) {
      throw new IllegalArgumentException("Calendar timezone cannot be null");
    }
    this.timezone = timezone;
  }

  /**
   * Gets the calendar model that manages events for this calendar.
   *
   * @return the calendar model
   */
  public CalendarModelInterface getModel() {
    return model;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Calendar calendar = (Calendar) o;
    return Objects.equals(name, calendar.name)
        && Objects.equals(timezone, calendar.timezone);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, timezone);
  }

  @Override
  public String toString() {
    return "Calendar{name='" + name + "', timezone=" + timezone + "}";
  }
}
