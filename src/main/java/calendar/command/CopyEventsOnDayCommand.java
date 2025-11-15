package calendar.command;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.CalendarModelInterface;
import calendar.model.EventInterface;
import calendar.utils.TimezoneUtils;
import calendar.view.ViewInterface;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to copy all events on a specific day to a target calendar.
 * Format: copy events on &lt;dateString&gt; --target &lt;calendarName&gt; to &lt;dateString&gt;
 *
 * <p>All events scheduled on the source date are copied to the target date.
 * Times physically remain the same but are converted to the timezone of the target calendar.
 * Example: 2pm EST event → 11am PST event (same moment in time, different local time).
 *
 * <p>DESIGN RATIONALE:
 * - Preserves absolute time by converting timezones
 * - Copies all events that start on the specified date
 * - Maintains event durations
 * - Creates new events (new IDs) in target calendar
 * - Handles conflicts by skipping events that can't be created
 */
public class CopyEventsOnDayCommand implements CommandInterface {
  private final String sourceDate;
  private final String targetCalendarName;
  private final String targetDate;

  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Creates a command to copy all events on a specific day.
   *
   * @param sourceDate the source date (yyyy-MM-dd)
   * @param targetCalendarName the name of the target calendar
   * @param targetDate the target date (yyyy-MM-dd)
   */
  public CopyEventsOnDayCommand(String sourceDate, String targetCalendarName, String targetDate) {
    this.sourceDate = sourceDate;
    this.targetCalendarName = targetCalendarName;
    this.targetDate = targetDate;
  }

  @Override
  public boolean execute(CalendarManager manager, ViewInterface view) throws IOException {
    
    Calendar sourceCalendar = manager.getCurrentCalendar();
    if (sourceCalendar == null) {
      view.displayMessage("Error: No calendar is currently in use. "
          + "Use 'use calendar --name <name>' first.");
      return false;
    }

    
    Calendar targetCalendar = manager.getCalendar(targetCalendarName);
    if (targetCalendar == null) {
      view.displayMessage("Error: Target calendar '" + targetCalendarName + "' not found.");
      return false;
    }

    
    LocalDate sourceLocalDate;
    LocalDate targetLocalDate;
    try {
      sourceLocalDate = LocalDate.parse(sourceDate, DATE_FORMATTER);
      targetLocalDate = LocalDate.parse(targetDate, DATE_FORMATTER);
    } catch (DateTimeParseException e) {
      view.displayMessage("Error: Invalid date format. Use yyyy-MM-dd.");
      return false;
    }

    
    List<EventInterface> allEvents = sourceCalendar.getModel().getAllEvents();
    List<EventInterface> eventsOnDay = new ArrayList<>();
    for (EventInterface event : allEvents) {
      if (event.getStartDateTime().toLocalDate().equals(sourceLocalDate)) {
        eventsOnDay.add(event);
      }
    }

    if (eventsOnDay.isEmpty()) {
      view.displayMessage("No events found on " + sourceDate + " in calendar '"
          + sourceCalendar.getName() + "'.");
      return false;
    }

    
    int copiedCount = 0;
    int failedCount = 0;

    for (EventInterface sourceEvent : eventsOnDay) {
      
      LocalDateTime sourceStart = sourceEvent.getStartDateTime();
      LocalDateTime sourceEnd = sourceEvent.getEndDateTime();

      
      LocalDateTime targetStart = TimezoneUtils.convertTimezone(
          sourceStart,
          sourceCalendar.getTimezone(),
          targetCalendar.getTimezone()
      );

      LocalDateTime targetEnd = TimezoneUtils.convertTimezone(
          sourceEnd,
          sourceCalendar.getTimezone(),
          targetCalendar.getTimezone()
      );

      
      int daysDiff = (int) java.time.temporal.ChronoUnit.DAYS.between(
          sourceStart.toLocalDate(),
          targetLocalDate
      );

      targetStart = targetStart.plusDays(daysDiff);
      targetEnd = targetEnd.plusDays(daysDiff);

      
      calendar.model.Event newEvent = new calendar.model.Event(
          sourceEvent.getSubject(),
          targetStart,
          targetEnd,
          sourceEvent.getDescription().orElse(null),
          sourceEvent.getLocation().orElse(null),
          sourceEvent.isPrivate(),
          java.util.UUID.randomUUID(),  
          null  
      );

      if (targetCalendar.getModel().createEvent(newEvent)) {
        copiedCount++;
      } else {
        failedCount++;
      }
    }

    view.displayMessage("Copied " + copiedCount + " event(s) from " + sourceDate
        + " in '" + sourceCalendar.getName() + "' to " + targetDate + " in '"
        + targetCalendarName + "'."
        + (failedCount > 0 ? " (" + failedCount + " failed due to conflicts)" : ""));

    return copiedCount > 0;
  }
}
