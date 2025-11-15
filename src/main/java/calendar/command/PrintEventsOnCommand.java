package calendar.command;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.CalendarModelInterface;
import calendar.model.EventInterface;
import calendar.util.DateTimeParser;
import calendar.view.ViewInterface;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Command to print events on a specific date.
 */
public class PrintEventsOnCommand implements CommandInterface {
  private final String dateString;

  /**
   * Creates a PrintEventsOnCommand.
   *
   * @param dateString the date string in format YYYY-MM-DD
   */
  public PrintEventsOnCommand(String dateString) {
    this.dateString = dateString;
  }

  @Override
  public boolean execute(CalendarManager manager, ViewInterface view) throws IOException {
    try {
      
      Calendar currentCal = manager.getCurrentCalendar();
      if (currentCal == null) {
        view.displayError("No calendar selected. Use 'use calendar --name <name>' first.");
        return false;
      }
      CalendarModelInterface model = currentCal.getModel();

      LocalDate date = DateTimeParser.parseDate(dateString);
      List<EventInterface> events = model.getEventsOnDate(date);
      view.displayEvents(events);
      return true;
    } catch (Exception e) {
      view.displayError("Failed to print events: " + e.getMessage());
      return false;
    }
  }
}

