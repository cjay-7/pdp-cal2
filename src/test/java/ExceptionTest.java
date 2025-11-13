import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import calendar.exceptions.DuplicateEventException;
import calendar.exceptions.EventNotFoundException;
import calendar.exceptions.FileExportException;
import calendar.exceptions.InvalidCommandException;
import java.io.IOException;
import org.junit.Test;

/**
 * Tests for exception classes.
 */
public class ExceptionTest {

  @Test
  public void testInvalidCommandException() {
    InvalidCommandException e = new InvalidCommandException("Invalid command");
    assertNotNull(e);
    assertEquals("Invalid command", e.getMessage());
  }

  @Test
  public void testDuplicateEventException() {
    DuplicateEventException e = new DuplicateEventException("Duplicate event");
    assertNotNull(e);
    assertEquals("Duplicate event", e.getMessage());
  }

  @Test
  public void testEventNotFoundException() {
    EventNotFoundException e = new EventNotFoundException("Event not found");
    assertNotNull(e);
    assertEquals("Event not found", e.getMessage());
  }

  @Test
  public void testFileExportException() {
    Throwable cause = new IOException("disk full");
    FileExportException e = new FileExportException("Failed", cause);
    assertNotNull(e);
    assertEquals("Failed", e.getMessage());
  }


  @Test
  public void testExceptionWithNullMessage() {
    InvalidCommandException e = new InvalidCommandException(null);
    assertNotNull(e);
  }
}

