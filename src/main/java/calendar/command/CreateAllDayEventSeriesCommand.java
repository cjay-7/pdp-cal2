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
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

/**
 * Command to create an all-day event series.
 */
public class CreateAllDayEventSeriesCommand implements CommandInterface {
  private final String subject;
  private final String dateString;
  private final String weekdaysString;
  private final Integer occurrences;
  private final String endDateString;
  private final boolean usesEndDate;

  /**
   * Creates a CreateAllDayEventSeriesCommand.
   *
   * @param subject        the event subject
   * @param dateString     the date string
   * @param weekdaysString the weekdays string (e.g., "MWF")
   * @param occurrences    number of occurrences (null if using endDate)
   * @param endDateString  end date string (null if using occurrences)
   * @param usesEndDate    true if using end date, false if using occurrences
   */
  public CreateAllDayEventSeriesCommand(String subject, String dateString, String weekdaysString,
                                        Integer occurrences, String endDateString,
                                        boolean usesEndDate) {
    this.subject = subject;
    this.dateString = dateString;
    this.weekdaysString = weekdaysString;
    this.occurrences = occurrences;
    this.endDateString = endDateString;
    this.usesEndDate = usesEndDate;
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

      
      Weekday[] weekdays = Weekday.parseString(weekdaysString);
      Set<java.time.DayOfWeek> dayOfWeekSet = Weekday.toDayOfWeekSet(weekdays);


      LocalDate date = DateTimeParser.parseDate(dateString);
      LocalDateTime start = LocalDateTime.of(date, LocalTime.of(Event.ALL_DAY_EVENT_START_HOUR, 0));
      LocalDateTime end = LocalDateTime.of(date, LocalTime.of(Event.ALL_DAY_EVENT_END_HOUR, 0));

      UUID seriesId = UUID.randomUUID();
      EventInterface template = new Event(subject, start, end, null, null, false,
          UUID.randomUUID(), seriesId);

      
      LocalDate endDate = null;
      Integer occurrencesCount = null;
      if (usesEndDate) {
        endDate = DateTimeParser.parseDate(endDateString);
      } else {
        occurrencesCount = occurrences;
      }

      
      EventSeries series = new EventSeries(seriesId, template, dayOfWeekSet,
          endDate, occurrencesCount, usesEndDate);

      boolean ok = model.createEventSeries(series);
      if (ok) {
        view.displayMessage("Created all-day event series: " + subject);
      } else {
        view.displayError("Failed to create series: duplicate events detected");
      }
      return ok;
    } catch (Exception e) {
      view.displayError("Failed to create all-day event series: " + e.getMessage());
      return false;
    }
  }
}

