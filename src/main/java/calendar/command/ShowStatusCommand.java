package calendar.command;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.CalendarModelInterface;
import calendar.util.DateTimeParser;
import calendar.view.ViewInterface;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Command to show busy status at a specific date/time.
 */
public class ShowStatusCommand implements CommandInterface {
  private final String dateTimeString;

  /**
   * Creates a ShowStatusCommand.
   *
   * @param dateTimeString the datetime string
   */
  public ShowStatusCommand(String dateTimeString) {
    this.dateTimeString = dateTimeString;
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

      LocalDateTime dateTime = DateTimeParser.parseDateTime(dateTimeString);
      boolean isBusy = model.isBusy(dateTime);
      String status = isBusy ? "busy" : "available";
      view.displayMessage(status);
      return true;
    } catch (Exception e) {
      view.displayError("Failed to show status: " + e.getMessage());
      return false;
    }
  }
}

