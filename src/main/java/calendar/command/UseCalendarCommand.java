package calendar.command;

import calendar.model.CalendarManager;
import calendar.model.CalendarModelInterface;
import calendar.view.ViewInterface;
import java.io.IOException;

/**
 * Command to set the current active calendar context.
 * Format: use calendar --name &lt;name-of-calendar&gt;
 *
 * <p>DESIGN RATIONALE:
 * - Sets the calendar context for subsequent event operations
 * - All create/edit/print/export event commands operate on the current calendar
 * - Validates that the calendar exists before setting it as current
 * - Provides clear feedback about which calendar is now in use
 */
public class UseCalendarCommand implements CommandInterface {
  private final String calendarName;

  /**
   * Creates a command to set the current calendar.
   *
   * @param calendarName the name of the calendar to use
   */
  public UseCalendarCommand(String calendarName) {
    this.calendarName = calendarName;
  }

  @Override
  public boolean execute(CalendarManager manager, ViewInterface view) throws IOException {
    boolean success = manager.setCurrentCalendar(calendarName);

    if (success) {
      view.displayMessage("Now using calendar '" + calendarName + "'.");
      return true;
    } else {
      view.displayMessage("Error: Calendar '" + calendarName + "' not found.");
      return false;
    }
  }
}
