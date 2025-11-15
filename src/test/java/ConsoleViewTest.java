import static org.junit.Assert.assertTrue;

import calendar.model.Event;
import calendar.model.EventInterface;
import calendar.view.ConsoleView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for ConsoleView class.
 */
public class ConsoleViewTest {
  private ByteArrayOutputStream outContent;
  private PrintStream originalOut;
  private ConsoleView view;

  /**
   * Sets up a fresh ConsoleView and output capture before each test.
   */
  @Before
  public void setUp() {
    originalOut = System.out;
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
    view = new ConsoleView(System.out);
  }

  /**
   * Restores System.out after each test.
   */
  @After
  public void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  public void testDisplayMessage() throws IOException {
    view.displayMessage("Test message");
    String output = outContent.toString();
    assertTrue(output.contains("Test message"));
  }

  @Test
  public void testDisplayError() throws IOException {
    view.displayError("Error occurred");
    String output = outContent.toString();
    assertTrue(output.contains("ERROR:"));
    assertTrue(output.contains("Error occurred"));
  }

  @Test
  public void testDisplayEventsEmpty() throws IOException {
    List<EventInterface> events = new ArrayList<>();
    view.displayEvents(events);
    String output = outContent.toString();
    assertTrue(output.contains("No events found"));
  }

  @Test
  
  
  
  public void testDisplayEventsSingle() throws IOException {
    EventInterface event = new Event("Meeting",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, UUID.randomUUID(), null);
    view.displayEvents(List.of(event));
    String output = outContent.toString();
    assertTrue(output.contains("Meeting"));
    assertTrue(output.contains("2025-06-01"));
    assertTrue(output.contains("10:00"));
    assertTrue(output.contains("11:00"));
  }

  @Test
  public void testDisplayEventsWithLocation() throws IOException {
    EventInterface event = new Event("Meeting",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, "Room 101", false, UUID.randomUUID(), null);
    view.displayEvents(List.of(event));
    String output = outContent.toString();
    assertTrue(output.contains("location: Room 101"));
  }

  @Test
  
  
  
  public void testDisplayEventsMultiple() throws IOException {
    EventInterface event1 = new Event("Meeting1",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 6, 1, 11, 0),
        null, null, false, UUID.randomUUID(), null);
    EventInterface event2 = new Event("Meeting2",
        LocalDateTime.of(2025, 6, 1, 14, 0),
        LocalDateTime.of(2025, 6, 1, 15, 0),
        null, null, false, UUID.randomUUID(), null);
    view.displayEvents(Arrays.asList(event1, event2));
    String output = outContent.toString();
    assertTrue(output.contains("Meeting1"));
    assertTrue(output.contains("Meeting2"));
  }
}

