import calendar.model.EventInterface;
import calendar.view.ViewInterface;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test implementation of ViewInterface that tracks all method calls for verification in tests.
 */
public class TestView implements ViewInterface {
  private final List<String> messages = new ArrayList<>();
  private final List<String> errors = new ArrayList<>();
  private final List<List<EventInterface>> displayedEvents = new ArrayList<>();

  @Override
  public void displayMessage(String message) throws IOException {
    messages.add(message);
  }

  @Override
  public void displayError(String error) throws IOException {
    errors.add(error);
  }

  @Override
  public void displayEvents(List<EventInterface> events) throws IOException {
    displayedEvents.add(new ArrayList<>(events));
  }

  public List<String> getMessages() {
    return new ArrayList<>(messages);
  }

  public List<String> getErrors() {
    return new ArrayList<>(errors);
  }

  public List<List<EventInterface>> getDisplayedEvents() {
    return new ArrayList<>(displayedEvents);
  }

  /**
   * Checks if any message contains the specified substring.
   *
   * @param message the substring to search for in messages
   * @return true if any message contains the substring, false otherwise
   */
  public boolean hasMessage(String message) {
    return messages.stream().anyMatch(m -> m.contains(message));
  }

  /**
   * Checks if any error message contains the specified substring.
   *
   * @param error the substring to search for in error messages
   * @return true if any error contains the substring, false otherwise
   */
  public boolean hasError(String error) {
    return errors.stream().anyMatch(e -> e.contains(error));
  }

  public int getMessageCount() {
    return messages.size();
  }

  public int getErrorCount() {
    return errors.size();
  }

  /**
   * Clears all stored messages, errors, and displayed events.
   */
  public void clear() {
    messages.clear();
    errors.clear();
    displayedEvents.clear();
  }
}
