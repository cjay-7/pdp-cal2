package calendar.exceptions;

/**
 * Thrown when exporting calendar data fails.
 */
public class FileExportException extends RuntimeException {
  /**
   * Creates a FileExportException.
   *
   * @param message the error message
   * @param cause   the underlying cause
   */
  public FileExportException(String message, Throwable cause) {
    super(message, cause);
  }
}
