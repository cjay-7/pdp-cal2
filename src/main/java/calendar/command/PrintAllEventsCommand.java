package calendar.command;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.CalendarModelInterface;
import calendar.model.EventInterface;
import calendar.view.ViewInterface;
import java.io.IOException;
import java.util.List;

/**
 * Command to print all events in the calendar.
 */
public class PrintAllEventsCommand implements CommandInterface {

  /**
   * Creates a PrintAllEventsCommand.
   */
  public PrintAllEventsCommand() {
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

      List<EventInterface> events = model.getAllEvents();
      view.displayEvents(events);
      return true;
    } catch (Exception e) {
      view.displayError("Failed to print events: " + e.getMessage());
      return false;
    }
  }
}

