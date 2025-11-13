package calendar.command;

import calendar.model.CalendarManager;
import calendar.view.ViewInterface;
import java.io.IOException;

/**
 * Represents a parsed command.
 *
 * <p>DESIGN CHANGE (HW5):
 * Updated to work with CalendarManager instead of CalendarModelInterface.
 * Commands can access the current calendar model via manager.getCurrentCalendar().getModel().
 * Calendar management commands work directly with the CalendarManager.
 */
public interface CommandInterface {

  /**
   * Executes the command.
   *
   * @param manager the calendar manager
   * @param view  the view for output
   * @return true if succeeded, false otherwise
   * @throws IOException if I/O fails
   */
  boolean execute(CalendarManager manager, ViewInterface view) throws IOException;
}

