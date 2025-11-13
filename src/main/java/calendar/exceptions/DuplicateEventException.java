package calendar.exceptions;

/**
 * Thrown when creating or editing would create a duplicate event.
 */
public class DuplicateEventException extends RuntimeException {
  /**
   * Creates a DuplicateEventException.
   *
   * @param message the error message
   */
  public DuplicateEventException(String message) {
    super(message);
  }
}
