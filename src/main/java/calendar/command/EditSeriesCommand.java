package calendar.command;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.CalendarModelInterface;
import calendar.model.EditSpec;
import calendar.model.EventInterface;
import calendar.model.EventStatus;
import calendar.util.DateTimeParser;
import calendar.view.ViewInterface;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Command to edit an entire series.
 * Format: edit series property subject from start with new value
 */
public class EditSeriesCommand implements CommandInterface {
  private final String property;
  private final String subject;
  private final String startString;
  private final String newValue;

  /**
   * Creates an EditSeriesCommand.
   *
   * @param property    the property to edit
   * @param subject     the event subject to find
   * @param startString the start datetime string
   * @param newValue    the new value for the property
   */
  public EditSeriesCommand(String property, String subject, String startString, String newValue) {
    this.property = property.toLowerCase();
    this.subject = subject;
    this.startString = startString;
    this.newValue = newValue;
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

      // Find the event by subject and start time
      LocalDateTime start = DateTimeParser.parseDateTime(startString);

      // Search all events for matching subject and start time
      // This handles recurring series where the specified start date may not match
      // the actual first occurrence (e.g., specifying Friday for a Tuesday series)
      EventInterface event = model.getAllEvents().stream()
          .filter(e -> e.getSubject().equals(subject)
              && e.getStartDateTime().equals(start))
          .findFirst()
          .orElse(null);

      // If not found by exact match, try finding by subject and series membership
      if (event == null) {
        event = model.getAllEvents().stream()
            .filter(e -> e.getSubject().equals(subject) && e.getSeriesId().isPresent())
            .findFirst()
            .orElse(null);
      }

      if (event == null) {
        view.displayError("Event not found: " + subject + " at " + startString);
        return false;
      }

      // Get series ID if exists
      UUID seriesId = event.getSeriesId().orElse(null);

      // Create EditSpec
      EditSpec spec = createEditSpec(property, newValue);

      // Edit the entire series or single event
      boolean success;
      if (seriesId != null) {
        success = model.editEntireSeries(seriesId, spec);
      } else {
        // Not a series, edit as single event
        success = model.editEvent(event.getId(), spec);
      }

      if (success) {
        view.displayMessage("Series edited successfully");
      } else {
        view.displayError("Failed to edit: would create duplicate event");
      }
      return success;
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      view.displayError("Failed to edit series: " + e.getMessage());
      return false;
    }
  }

  /**
   * Creates an EditSpec for the given property and value.
   */
  private EditSpec createEditSpec(String property, String value) {
    switch (property) {
      case "subject":
        return new EditSpec(value, null, null, null, null, null);
      case "start":
        LocalDateTime newStart = DateTimeParser.parseDateTime(value);
        return new EditSpec(null, newStart, null, null, null, null);
      case "end":
        LocalDateTime newEnd = DateTimeParser.parseDateTime(value);
        return new EditSpec(null, null, newEnd, null, null, null);
      case "description":
        return new EditSpec(null, null, null, value, null, null);
      case "location":
        return new EditSpec(null, null, null, null, value, null);
      case "status":
        EventStatus status = EventStatus.fromString(value);
        return new EditSpec(null, null, null, null, null, status);
      default:
        throw new IllegalArgumentException("Invalid property: " + property);
    }
  }
}

