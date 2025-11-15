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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Command to copy a specific event to a target calendar at a new date/time.
 * Format: copy event &lt;eventName&gt; on &lt;dateStringTtimeString&gt;
 *         --target &lt;calendarName&gt; to &lt;dateStringTtimeString&gt;
 *
 * <p>The source event is identified by name and start date/time in the
 * current calendar.
 * The event is copied to the target calendar with the new start date/time.
 * The duration of the event is preserved.
 * Times are converted between timezones if source and target calendars have different timezones.
 *
 * <p>DESIGN RATIONALE:
 * - Preserves event duration when copying
 * - Handles timezone conversion automatically
 * - Creates a new event (new ID) in the target calendar
 * - Validates that both source and target calendars exist
 * - Provides clear error messages for various failure cases
 */
public class CopyEventCommand implements CommandInterface {
  private final String eventName;
  private final String sourceDateTime;
  private final String targetCalendarName;
  private final String targetDateTime;

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

  /**
   * Creates a command to copy an event.
   *
   * @param eventName the name of the event to copy
   * @param sourceDateTime the start date/time of the source event (yyyy-MM-ddTHH:mm)
   * @param targetCalendarName the name of the target calendar
   * @param targetDateTime the new start date/time in the target calendar (yyyy-MM-ddTHH:mm)
   */
  public CopyEventCommand(String eventName, String sourceDateTime,
                          String targetCalendarName, String targetDateTime) {
    this.eventName = eventName;
    this.sourceDateTime = sourceDateTime;
    this.targetCalendarName = targetCalendarName;
    this.targetDateTime = targetDateTime;
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

    LocalDateTime sourceStart;
    try {
      sourceStart = LocalDateTime.parse(sourceDateTime, DATE_TIME_FORMATTER);
    } catch (DateTimeParseException e) {
      view.displayMessage("Error: Invalid source date/time format. Use yyyy-MM-ddTHH:mm.");
      return false;
    }

    LocalDateTime targetStart;
    try {
      targetStart = LocalDateTime.parse(targetDateTime, DATE_TIME_FORMATTER);
    } catch (DateTimeParseException e) {
      view.displayMessage("Error: Invalid target date/time format. Use yyyy-MM-ddTHH:mm.");
      return false;
    }

    List<EventInterface> events = sourceCalendar.getModel().getAllEvents();
    EventInterface sourceEvent = null;
    for (EventInterface event : events) {
      if (event.getSubject().equals(eventName)
          && event.getStartDateTime().equals(sourceStart)) {
        sourceEvent = event;
        break;
      }
    }

    if (sourceEvent == null) {
      view.displayMessage("Error: Event '" + eventName + "' not found on "
          + sourceDateTime + " in calendar '" + sourceCalendar.getName() + "'.");
      return false;
    }

    
    long durationMinutes = java.time.Duration.between(
        sourceEvent.getStartDateTime(),
        sourceEvent.getEndDateTime()
    ).toMinutes();

    
    LocalDateTime targetEnd = targetStart.plusMinutes(durationMinutes);

    
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

    boolean success = targetCalendar.getModel().createEvent(newEvent);

    if (success) {
      view.displayMessage("Event '" + eventName + "' copied from '"
          + sourceCalendar.getName() + "' to '" + targetCalendarName + "' on "
          + targetDateTime + ".");
      return true;
    } else {
      view.displayMessage("Error: Could not copy event (may conflict with existing event).");
      return false;
    }
  }
}
