package calendar.exceptions;

/**
 * Thrown when a user enters an invalid command.
 */
public class InvalidCommandException extends RuntimeException {
  /**
   * Creates an InvalidCommandException.
   *
   * @param message the error message
   */
  public InvalidCommandException(String message) {
    super(message);
  }
}
