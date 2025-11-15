package calendar.command;

import calendar.model.CalendarManager;
import calendar.view.ViewInterface;
import java.io.IOException;

/**
 * Command to exit the application.
 */
public class ExitCommand implements CommandInterface {

  /**
   * Creates an ExitCommand.
   */
  public ExitCommand() {
  }

  @Override
  public boolean execute(CalendarManager manager, ViewInterface view) throws IOException {
    return false;
  }
}

