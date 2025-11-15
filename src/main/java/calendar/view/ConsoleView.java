package calendar.view;

import calendar.model.EventInterface;
import java.io.IOException;
import java.util.List;

/**
 * Console view implementation backed by an Appendable.
 */
public class ConsoleView implements ViewInterface {
  private final Appendable out;

  /**
   * Creates a ConsoleView.
   *
   * @param out the Appendable to write to
   */
  public ConsoleView(Appendable out) {
    this.out = out;
  }

  @Override
  public void displayMessage(String message) throws IOException {
    out.append(message).append(System.lineSeparator());
  }

  @Override
  public void displayError(String error) throws IOException {
    out.append("ERROR: ").append(error).append(System.lineSeparator());
  }

  @Override
  public void displayEvents(List<EventInterface> events) throws IOException {
    if (events.isEmpty()) {
      out.append("No events found.").append(System.lineSeparator());
      return;
    }

    for (EventInterface e : events) {
      
      
      out.append("- ").append(e.getSubject()).append(" starting on ")
          .append(formatDate(e.getStartDateTime().toLocalDate())).append(" at ")
          .append(formatTime(e.getStartDateTime().toLocalTime())).append(", ending on ")
          .append(formatDate(e.getEndDateTime().toLocalDate())).append(" at ")
          .append(formatTime(e.getEndDateTime().toLocalTime()));

      
      if (e.getLocation().isPresent()) {
        out.append(", location: ").append(e.getLocation().get());
      }

      out.append(System.lineSeparator());
    }
  }

  /**
   * Formats a date as YYYY-MM-DD.
   *
   * @param date the date to format
   * @return formatted date string
   */
  private String formatDate(java.time.LocalDate date) {
    return date.toString();
  }

  /**
   * Formats a time as HH:MM.
   *
   * @param time the time to format
   * @return formatted time string
   */
  private String formatTime(java.time.LocalTime time) {
    return String.format("%02d:%02d", time.getHour(), time.getMinute());
  }
}
