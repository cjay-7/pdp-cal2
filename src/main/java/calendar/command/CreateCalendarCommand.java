package calendar.command;

import calendar.model.CalendarManager;
import calendar.model.CalendarModelInterface;
import calendar.view.ViewInterface;
import java.io.IOException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;

/**
 * Command to create a new calendar with a unique name and timezone.
 * Format: create calendar --name &lt;calName&gt; --timezone area/location
 *
 * <p>DESIGN RATIONALE:
 * - Validates timezone using ZoneId.of() which throws ZoneRulesException for invalid timezones
 * - Checks for duplicate calendar names (case-insensitive) via CalendarManager
 * - Provides clear error messages for validation failures
 * - Follows existing command pattern structure for consistency
 */
public class CreateCalendarCommand implements CommandInterface {
  private final String name;
  private final String timezone;

  /**
   * Creates a command to create a new calendar.
   *
   * @param name the unique name for the calendar
   * @param timezone the timezone in IANA format (e.g., "America/New_York")
   */
  public CreateCalendarCommand(String name, String timezone) {
    this.name = name;
    this.timezone = timezone;
  }

  @Override
  public boolean execute(CalendarManager manager, ViewInterface view) throws IOException {
    // Validate timezone format
    ZoneId zoneId;
    try {
      zoneId = ZoneId.of(timezone);
    } catch (ZoneRulesException e) {
      view.displayMessage("Error: Invalid timezone '" + timezone + "'. "
          + "Use IANA format (e.g., America/New_York).");
      return false;
    }

    // Create calendar
    boolean success = manager.createCalendar(name, zoneId);

    if (success) {
      view.displayMessage("Calendar '" + name + "' created successfully with timezone "
          + timezone + ".");
      return true;
    } else {
      view.displayMessage("Error: Calendar '" + name + "' already exists.");
      return false;
    }
  }
}
