package calendar.exceptions;

/**
 * Thrown when a requested event cannot be found.
 */
public class EventNotFoundException extends RuntimeException {
  /**
   * Creates an EventNotFoundException.
   *
   * @param message the error message
   */
  public EventNotFoundException(String message) {
    super(message);
  }
}
