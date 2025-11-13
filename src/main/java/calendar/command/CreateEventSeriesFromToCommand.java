package calendar.command;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.CalendarModelInterface;
import calendar.model.Event;
import calendar.model.EventInterface;
import calendar.model.EventSeries;
import calendar.model.Weekday;
import calendar.util.DateTimeParser;
import calendar.view.ViewInterface;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Command to create an event series from a regular event (with from/to times).
 */
public class CreateEventSeriesFromToCommand implements CommandInterface {
  private final String subject;
  private final String from;
  private final String to;
  private final String weekdaysString;
  private final Integer occurrences;
  private final String endDateString;
  private final boolean usesEndDate;

  /**
   * Creates a CreateEventSeriesFromToCommand.
   *
   * @param subject        the event subject
   * @param from           the start datetime string
   * @param to             the end datetime string
   * @param weekdaysString the weekdays string (e.g., "MWF")
   * @param occurrences    number of occurrences (null if using endDate)
   * @param endDateString  end date string (null if using occurrences)
   * @param usesEndDate    true if using end date, false if using occurrences
   */
  public CreateEventSeriesFromToCommand(String subject, String from, String to,
                                        String weekdaysString, Integer occurrences,
                                        String endDateString, boolean usesEndDate) {
    this.subject = subject;
    this.from = from;
    this.to = to;
    this.weekdaysString = weekdaysString;
    this.occurrences = occurrences;
    this.endDateString = endDateString;
    this.usesEndDate = usesEndDate;
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

      // Parse weekdays
      Weekday[] weekdays = Weekday.parseString(weekdaysString);
      Set<java.time.DayOfWeek> dayOfWeekSet = Weekday.toDayOfWeekSet(weekdays);

      // Parse datetime
      LocalDateTime start = DateTimeParser.parseDateTime(from);
      LocalDateTime end = DateTimeParser.parseDateTime(to);

      // Create template event with series ID
      UUID seriesId = UUID.randomUUID();
      EventInterface template = new Event(subject, start, end, null, null, false,
          UUID.randomUUID(), seriesId);

      // Parse end condition
      LocalDate endDate = null;
      Integer occurrencesCount = null;
      if (usesEndDate) {
        endDate = DateTimeParser.parseDate(endDateString);
      } else {
        occurrencesCount = occurrences;
      }

      // Create series
      EventSeries series = new EventSeries(seriesId, template, dayOfWeekSet,
          endDate, occurrencesCount, usesEndDate);

      boolean ok = model.createEventSeries(series);
      if (ok) {
        view.displayMessage("Created event series: " + subject);
      } else {
        view.displayError("Failed to create series: duplicate events detected");
      }
      return ok;
    } catch (Exception e) {
      view.displayError("Failed to create event series: " + e.getMessage());
      return false;
    }
  }
}

