package calendar.view;

import calendar.model.EventInterface;
import java.io.IOException;
import java.util.List;

/**
 * View abstraction for displaying output.
 */
public interface ViewInterface {

  /**
   * Displays a message to the user.
   *
   * @param message the message to display
   * @throws IOException if I/O fails
   */
  void displayMessage(String message) throws IOException;

  /**
   * Displays an error message.
   *
   * @param error the error message
   * @throws IOException if I/O fails
   */
  void displayError(String error) throws IOException;

  /**
   * Displays a list of events.
   *
   * @param events the events to display
   * @throws IOException if I/O fails
   */
  void displayEvents(List<EventInterface> events) throws IOException;
}

