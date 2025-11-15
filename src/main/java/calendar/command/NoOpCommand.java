package calendar.command;

import calendar.exceptions.InvalidCommandException;
import calendar.model.CalendarManager;
import calendar.view.ViewInterface;
import java.io.IOException;

/**
 * A command that represents an unrecognized command.
 */
public class NoOpCommand implements CommandInterface {
  private final String input;

  /**
   * Creates a NoOpCommand for an unrecognized input.
   *
   * @param input the unrecognized input string
   */
  public NoOpCommand(String input) {
    this.input = input;
  }

  /**
   * Creates a NoOpCommand with empty input (for empty lines).
   */
  public NoOpCommand() {
    this.input = null;
  }

  @Override
  public boolean execute(CalendarManager manager, ViewInterface view) throws IOException {
    if (input != null && !input.trim().isEmpty()) {
      throw new InvalidCommandException("Invalid command: " + input);
    }
    return true;
  }
}
