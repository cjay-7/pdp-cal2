package calendar.command;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.CalendarModelInterface;
import calendar.model.EditSpec;
import calendar.model.EventInterface;
import calendar.util.DateTimeParser;
import calendar.util.EditSpecFactory;
import calendar.view.ViewInterface;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Command to edit events forward in a series.
 * Format: edit events property subject from start with new value
 */
public class EditEventsCommand implements CommandInterface {
  private final String property;
  private final String subject;
  private final String startString;
  private final String newValue;

  /**
   * Creates an EditEventsCommand.
   *
   * @param property    the property to edit
   * @param subject     the event subject to find
   * @param startString the start datetime string
   * @param newValue    the new value for the property
   */
  public EditEventsCommand(String property, String subject, String startString, String newValue) {
    this.property = property.toLowerCase();
    this.subject = subject;
    this.startString = startString;
    this.newValue = newValue;
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

      
      LocalDateTime start = DateTimeParser.parseDateTime(startString);
      LocalDate date = start.toLocalDate();

      
      EventInterface event = model.getEventsOnDate(date).stream()
          .filter(e -> e.getSubject().equals(subject)
              && e.getStartDateTime().equals(start))
          .findFirst()
          .orElse(null);

      if (event == null) {
        view.displayError("Event not found: " + subject + " at " + startString);
        return false;
      }

      
      LocalDate fromDate = start.toLocalDate();


      EditSpec spec = EditSpecFactory.createEditSpec(property, newValue);


      boolean success;
      if (event.getSeriesId().isPresent()) {
        UUID seriesId = event.getSeriesId().get();
        success = model.editSeriesFrom(seriesId, fromDate, spec);
      } else {

        success = model.editEvent(event.getId(), spec);
      }

      if (success) {
        view.displayMessage("Events edited successfully");
      } else {
        view.displayError("Failed to edit: would create duplicate event");
      }
      return success;
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      view.displayError("Failed to edit events: " + e.getMessage());
      return false;
    }
  }
}

