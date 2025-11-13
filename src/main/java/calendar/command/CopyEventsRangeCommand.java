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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Command to copy all events within a date range to a target calendar.
 * Format: copy events between &lt;dateString&gt; and &lt;dateString&gt;
 *         --target &lt;calendarName&gt; to &lt;dateString&gt;
 *
 * <p>Copies all events that overlap with the specified date interval
 * (inclusive endpoints).
 * If an event series partially overlaps the range, only overlapping events are copied.
 * Series status is retained in the destination calendar.
 *
 * <p>DESIGN RATIONALE:
 * - Endpoint dates are inclusive
 * - Preserves series relationships in target calendar
 * - Handles timezone conversion
 * - Creates new series IDs for copied series events
 * - Maintains event durations and relationships
 */
public class CopyEventsRangeCommand implements CommandInterface {
  private final String startDate;
  private final String endDate;
  private final String targetCalendarName;
  private final String targetStartDate;

  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Creates a command to copy events in a date range.
   *
   * @param startDate the start of the source date range (yyyy-MM-dd)
   * @param endDate the end of the source date range (yyyy-MM-dd)
   * @param targetCalendarName the name of the target calendar
   * @param targetStartDate the start date in the target calendar (yyyy-MM-dd)
   */
  public CopyEventsRangeCommand(String startDate, String endDate,
                                 String targetCalendarName, String targetStartDate) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.targetCalendarName = targetCalendarName;
    this.targetStartDate = targetStartDate;
  }

  @Override
  public boolean execute(CalendarManager manager, ViewInterface view) throws IOException {
    // Check current calendar exists
    Calendar sourceCalendar = manager.getCurrentCalendar();
    if (sourceCalendar == null) {
      view.displayMessage("Error: No calendar is currently in use. "
          + "Use 'use calendar --name <name>' first.");
      return false;
    }

    // Check target calendar exists
    Calendar targetCalendar = manager.getCalendar(targetCalendarName);
    if (targetCalendar == null) {
      view.displayMessage("Error: Target calendar '" + targetCalendarName + "' not found.");
      return false;
    }

    // Parse dates
    LocalDate sourceStart;
    LocalDate sourceEnd;
    LocalDate targetStart;
    try {
      sourceStart = LocalDate.parse(startDate, DATE_FORMATTER);
      sourceEnd = LocalDate.parse(endDate, DATE_FORMATTER);
      targetStart = LocalDate.parse(targetStartDate, DATE_FORMATTER);
    } catch (DateTimeParseException e) {
      view.displayMessage("Error: Invalid date format. Use yyyy-MM-dd.");
      return false;
    }

    if (sourceEnd.isBefore(sourceStart)) {
      view.displayMessage("Error: End date must be after or equal to start date.");
      return false;
    }

    // Find all events in the range
    List<EventInterface> allEvents = sourceCalendar.getModel().getAllEvents();
    List<EventInterface> eventsInRange = new ArrayList<>();

    for (EventInterface event : allEvents) {
      LocalDate eventDate = event.getStartDateTime().toLocalDate();
      // Check if event overlaps with range (inclusive)
      if (!eventDate.isBefore(sourceStart) && !eventDate.isAfter(sourceEnd)) {
        eventsInRange.add(event);
      }
    }

    if (eventsInRange.isEmpty()) {
      view.displayMessage("No events found between " + startDate + " and " + endDate
          + " in calendar '" + sourceCalendar.getName() + "'.");
      return false;
    }

    // Map old series IDs to new series IDs
    Map<UUID, UUID> seriesIdMap = new HashMap<>();

    // Calculate day offset
    long dayOffset = java.time.temporal.ChronoUnit.DAYS.between(sourceStart, targetStart);

    // Copy each event
    int copiedCount = 0;
    int failedCount = 0;

    for (EventInterface sourceEvent : eventsInRange) {
      // Convert times to target timezone
      LocalDateTime sourceStartTime = sourceEvent.getStartDateTime();
      LocalDateTime sourceEndTime = sourceEvent.getEndDateTime();

      LocalDateTime targetStartTime = TimezoneUtils.convertTimezone(
          sourceStartTime,
          sourceCalendar.getTimezone(),
          targetCalendar.getTimezone()
      );

      LocalDateTime targetEndTime = TimezoneUtils.convertTimezone(
          sourceEndTime,
          sourceCalendar.getTimezone(),
          targetCalendar.getTimezone()
      );

      // Adjust by day offset
      targetStartTime = targetStartTime.plusDays(dayOffset);
      targetEndTime = targetEndTime.plusDays(dayOffset);

      // Handle series ID
      UUID newSeriesId = null;
      if (sourceEvent.getSeriesId().isPresent()) {
        UUID oldSeriesId = sourceEvent.getSeriesId().get();
        if (seriesIdMap.containsKey(oldSeriesId)) {
          newSeriesId = seriesIdMap.get(oldSeriesId);
        } else {
          newSeriesId = UUID.randomUUID();
          seriesIdMap.put(oldSeriesId, newSeriesId);
        }
      }

      // Create the new event
      calendar.model.Event newEvent = new calendar.model.Event(
          sourceEvent.getSubject(),
          targetStartTime,
          targetEndTime,
          sourceEvent.getDescription().orElse(null),
          sourceEvent.getLocation().orElse(null),
          sourceEvent.isPrivate(),
          UUID.randomUUID(),  // New event ID
          newSeriesId         // Preserved series ID (or null)
      );

      if (targetCalendar.getModel().createEvent(newEvent)) {
        copiedCount++;
      } else {
        failedCount++;
      }
    }

    view.displayMessage("Copied " + copiedCount + " event(s) from " + startDate + " to "
        + endDate + " in '" + sourceCalendar.getName() + "' to target starting at "
        + targetStartDate + " in '" + targetCalendarName + "'."
        + (failedCount > 0 ? " (" + failedCount + " failed due to conflicts)" : ""));

    return copiedCount > 0;
  }
}
