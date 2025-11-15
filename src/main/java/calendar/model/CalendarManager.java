package calendar.model;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages multiple calendars and tracks the currently active calendar.
 * Ensures calendar names are unique (case-insensitive).
 *
 * <p>DESIGN RATIONALE:
 * - Uses HashMap for O(1) calendar lookups by name
 * - Case-insensitive name matching prevents duplicate calendars with different cases
 * - Tracks current calendar to support context-dependent operations
 * - Provides methods for CRUD operations on calendars
 *
 * <p>REPRESENTATION INVARIANTS:
 * - All calendar names in the map are unique (case-insensitive)
 * - If currentCalendar is not null, it must exist in the calendars map
 * - Map keys are calendar names (as stored in Calendar objects)
 */
public class CalendarManager {
  private final Map<String, Calendar> calendars;
  private Calendar currentCalendar;

  /**
   * Creates a new CalendarManager with no calendars.
   */
  public CalendarManager() {
    this.calendars = new HashMap<>();
    this.currentCalendar = null;
  }

  /**
   * Creates a new calendar with the specified name and timezone.
   * Calendar names must be unique (case-insensitive).
   *
   * @param name the unique name for the calendar
   * @param timezone the timezone for the calendar (IANA format)
   * @return true if calendar was created, false if name already exists
   * @throws IllegalArgumentException if name is null/empty or timezone is null
   */
  public boolean createCalendar(String name, ZoneId timezone) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Calendar name cannot be null or empty");
    }
    if (timezone == null) {
      throw new IllegalArgumentException("Calendar timezone cannot be null");
    }

    
    if (calendarExists(name)) {
      return false;
    }

    CalendarModelInterface model = new CalendarModel();
    Calendar calendar = new Calendar(name, timezone, model);
    calendars.put(name, calendar);
    return true;
  }

  /**
   * Gets a calendar by name (case-sensitive).
   *
   * @param name the name of the calendar to retrieve
   * @return the calendar with the specified name, or null if not found
   */
  public Calendar getCalendar(String name) {
    if (name == null) {
      return null;
    }
    return calendars.get(name);
  }

  /**
   * Sets the current active calendar.
   *
   * @param name the name of the calendar to set as current
   * @return true if successful, false if calendar doesn't exist
   */
  public boolean setCurrentCalendar(String name) {
    Calendar calendar = getCalendar(name);
    if (calendar == null) {
      return false;
    }
    this.currentCalendar = calendar;
    return true;
  }

  /**
   * Gets the currently active calendar.
   *
   * @return the current calendar, or null if none is set
   */
  public Calendar getCurrentCalendar() {
    return currentCalendar;
  }

  /**
   * Edits the name of an existing calendar.
   * The new name must be unique (case-insensitive).
   *
   * @param oldName the current name of the calendar
   * @param newName the new name for the calendar
   * @return true if successful, false if calendar doesn't exist or new name already exists
   * @throws IllegalArgumentException if newName is null or empty
   */
  public boolean editCalendarName(String oldName, String newName) {
    if (newName == null || newName.trim().isEmpty()) {
      throw new IllegalArgumentException("Calendar name cannot be null or empty");
    }

    Calendar calendar = getCalendar(oldName);
    if (calendar == null) {
      return false;
    }

    
    if (!oldName.equals(newName) && calendarExists(newName)) {
      return false;
    }

    
    calendars.remove(oldName);
    calendar.setName(newName);
    calendars.put(newName, calendar);

    return true;
  }

  /**
   * Edits the timezone of an existing calendar.
   *
   * @param name the name of the calendar
   * @param timezone the new timezone for the calendar
   * @return true if successful, false if calendar doesn't exist
   * @throws IllegalArgumentException if timezone is null
   */
  public boolean editCalendarTimezone(String name, ZoneId timezone) {
    if (timezone == null) {
      throw new IllegalArgumentException("Calendar timezone cannot be null");
    }

    Calendar calendar = getCalendar(name);
    if (calendar == null) {
      return false;
    }

    calendar.setTimezone(timezone);
    return true;
  }

  /**
   * Gets all calendars.
   *
   * @return a list of all calendars
   */
  public List<Calendar> getAllCalendars() {
    return new ArrayList<>(calendars.values());
  }

  /**
   * Checks if a calendar with the given name exists (case-insensitive).
   *
   * @param name the name to check
   * @return true if a calendar with this name exists (case-insensitive), false otherwise
   */
  private boolean calendarExists(String name) {
    if (name == null) {
      return false;
    }
    return calendars.keySet().stream()
        .anyMatch(key -> key.equalsIgnoreCase(name));
  }
}
