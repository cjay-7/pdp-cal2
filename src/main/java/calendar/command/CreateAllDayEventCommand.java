package calendar.command;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.CalendarModelInterface;
import calendar.model.Event;
import calendar.model.EventInterface;
import calendar.util.DateTimeParser;
import calendar.view.ViewInterface;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Command to create an all-day event.
 * All-day events are defined as 8am-5pm on the specified date.
 */
public class CreateAllDayEventCommand implements CommandInterface {
  private final String subject;
  private final String dateString;

  /**
   * Creates a CreateAllDayEventCommand.
   *
   * @param subject    the event subject
   * @param dateString the date string in format YYYY-MM-DD
   */
  public CreateAllDayEventCommand(String subject, String dateString) {
    this.subject = subject;
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
      LocalTime startTime = LocalTime.of(Event.ALL_DAY_EVENT_START_HOUR, 0);
      LocalTime endTime = LocalTime.of(Event.ALL_DAY_EVENT_END_HOUR, 0);
      LocalDateTime start = LocalDateTime.of(date, startTime);
      LocalDateTime end = LocalDateTime.of(date, endTime);

      EventInterface event =
          new Event(subject, start, end, null, null, false, UUID.randomUUID(), null);
      boolean ok = model.createEvent(event);
      if (ok) {
        view.displayMessage("Created all-day event: " + subject);
      } else {
        view.displayError("Duplicate event: " + subject);
      }
      return ok;
    } catch (Exception e) {
      view.displayError("Failed to create all-day event: " + e.getMessage());
      return false;
    }
  }
}
