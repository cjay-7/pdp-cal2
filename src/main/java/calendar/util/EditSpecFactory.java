package calendar.util;

import calendar.model.EditSpec;
import calendar.model.EventStatus;
import java.time.LocalDateTime;

/**
 * Factory class for creating EditSpec objects based on property names and values.
 */
public class EditSpecFactory {

  /**
   * Creates an EditSpec for the given property and value.
   *
   * @param property the property name (subject, start, end, description, location, status)
   * @param value    the new value for the property
   * @return EditSpec with only the specified property set
   * @throws IllegalArgumentException if the property name is invalid
   */
  public static EditSpec createEditSpec(String property, String value) {
    switch (property.toLowerCase()) {
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
