package calendar.command;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.CalendarModelInterface;
import calendar.model.EventInterface;
import calendar.util.DateTimeParser;
import calendar.view.ViewInterface;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Command to print events in a date/time range.
 */
public class PrintEventsRangeCommand implements CommandInterface {
  private final String startString;
  private final String endString;

  /**
   * Creates a PrintEventsRangeCommand.
   *
   * @param startString the start datetime string
   * @param endString   the end datetime string
   */
  public PrintEventsRangeCommand(String startString, String endString) {
    this.startString = startString;
    this.endString = endString;
  }

  @Override
  public boolean execute(CalendarManager manager, ViewInterface view) throws IOException {
    try {
      // Get current calendar
      Calendar currentCal = manager.getCurrentCalendar();
      if (currentCal == null) {
        view.displayError("No calendar selected. Use 'use calendar --name <name>' first.");
        return false;
      }
      CalendarModelInterface model = currentCal.getModel();

      LocalDateTime start = DateTimeParser.parseDateTime(startString);
      LocalDateTime end = DateTimeParser.parseDateTime(endString);
      List<EventInterface> events = model.getEventsInRange(start, end);
      view.displayEvents(events);
      return true;
    } catch (Exception e) {
      view.displayError("Failed to print events: " + e.getMessage());
      return false;
    }
  }
}

