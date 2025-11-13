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
import java.time.LocalDateTime;

/**
 * Command to edit a single event.
 * Format: edit event property subject from start to end with new value
 */
public class EditEventCommand implements CommandInterface {
  private final String property;
  private final String subject;
  private final String startString;
  private final String endString;
  private final String newValue;

  /**
   * Creates an EditEventCommand.
   *
   * @param property    the property to edit (subject, start, end, description, location, status)
   * @param subject     the event subject to find
   * @param startString the start datetime string
   * @param endString   the end datetime string
   * @param newValue    the new value for the property
   */
  public EditEventCommand(String property, String subject, String startString, String endString,
                          String newValue) {
    this.property = property.toLowerCase();
    this.subject = subject;
    this.startString = startString;
    this.endString = endString;
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

      // Find the event
      LocalDateTime start = DateTimeParser.parseDateTime(startString);
      LocalDateTime end = DateTimeParser.parseDateTime(endString);
      EventInterface event = model.findEventByProperties(subject, start, end);

      if (event == null) {
        view.displayError("Event not found: " + subject);
        return false;
      }

      // Create EditSpec based on property (may throw IllegalArgumentException)
      EditSpec spec = createEditSpec(property, newValue);

      // Edit the event
      boolean success = model.editEvent(event.getId(), spec);
      if (success) {
        view.displayMessage("Event edited successfully");
      } else {
        view.displayError("Failed to edit: would create duplicate event");
      }
      return success;
    } catch (IllegalArgumentException e) {
      // Propagate IllegalArgumentException for invalid properties
      throw e;
    } catch (Exception e) {
      view.displayError("Failed to edit event: " + e.getMessage());
      return false;
    }
  }

  /**
   * Creates an EditSpec for the given property and value.
   *
   * @param property the property name
   * @param value    the new value
   * @return EditSpec with only the specified property set
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

