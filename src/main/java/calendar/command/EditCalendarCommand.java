package calendar.command;

import calendar.model.CalendarManager;
import calendar.model.CalendarModelInterface;
import calendar.view.ViewInterface;
import java.io.IOException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;

/**
 * Command to edit properties of an existing calendar.
 * Format: edit calendar --name &lt;name-of-calendar&gt;
 *         --property &lt;property-name&gt; &lt;new-value&gt;
 *
 * <p>Supported properties:
 * - name: Changes the calendar's name (must be unique)
 * - timezone: Changes the calendar's timezone (must be valid IANA format)
 *
 * <p>DESIGN RATIONALE:
 * - Validates property names before attempting modifications
 * - Provides specific error messages for different failure cases
 * - Maintains calendar name uniqueness constraint
 * - Validates timezone format using ZoneId.of()
 */
public class EditCalendarCommand implements CommandInterface {
  private final String calendarName;
  private final String property;
  private final String newValue;

  /**
   * Creates a command to edit a calendar property.
   *
   * @param calendarName the name of the calendar to edit
   * @param property the property to modify ("name" or "timezone")
   * @param newValue the new value for the property
   */
  public EditCalendarCommand(String calendarName, String property, String newValue) {
    this.calendarName = calendarName;
    this.property = property;
    this.newValue = newValue;
  }

  @Override
  public boolean execute(CalendarManager manager, ViewInterface view) throws IOException {
    boolean success = false;

    switch (property.toLowerCase()) {
      case "name":
        success = manager.editCalendarName(calendarName, newValue);
        if (success) {
          view.displayMessage("Calendar name changed from '" + calendarName
              + "' to '" + newValue + "'.");
        } else {
          view.displayMessage("Error: Calendar '" + calendarName + "' not found or name '"
              + newValue + "' already exists.");
        }
        break;

      case "timezone":
        // Validate timezone format
        try {
          ZoneId zoneId = ZoneId.of(newValue);
          success = manager.editCalendarTimezone(calendarName, zoneId);
          if (success) {
            view.displayMessage("Calendar '" + calendarName + "' timezone changed to '"
                + newValue + "'.");
          } else {
            view.displayMessage("Error: Calendar '" + calendarName + "' not found.");
          }
        } catch (ZoneRulesException e) {
          view.displayMessage("Error: Invalid timezone '" + newValue + "'. "
              + "Use IANA format (e.g., America/New_York).");
          success = false;
        }
        break;

      default:
        view.displayMessage("Error: Unknown property '" + property + "'. "
            + "Valid properties are 'name' and 'timezone'.");
        break;
    }

    return success;
  }
}
