package calendar.controller;

import calendar.command.CommandInterface;
import calendar.command.CommandParser;
import calendar.exceptions.InvalidCommandException;
import calendar.model.CalendarManager;
import calendar.view.ViewInterface;
import java.io.IOException;
import java.util.Scanner;

/**
 * Unified controller that reads commands from any Readable source.
 *
 * <p>This controller eliminates duplication between interactive and headless modes
 * by accepting a Readable input source. It can read from System.in, FileReader,
 * StringReader, or any other Readable implementation.
 *
 * <p>DESIGN CHECK: IO Abstraction
 * The controller uses Readable instead of concrete types like System.in or FileReader.
 * This demonstrates complete decoupling from the type of IO device - the controller
 * can read from any Readable implementation (console, file, string, network, etc.)
 * without modification. The abstraction allows for easier testing with StringReader
 * and flexibility in the source of commands.
 *
 * <p>DESIGN CHANGE (HW5):
 * Updated to work with CalendarManager instead of CalendarModelInterface.
 * The controller now manages multiple calendars through CalendarManager and
 * delegates to the appropriate calendar based on user context.
 */
public class Controller implements ControllerInterface {
  private final CalendarManager manager;
  private final ViewInterface view;
  private final CommandParser parser;
  private final Readable input;
  private final boolean interactive;

  /**
   * Creates a Controller.
   *
   * @param manager     the calendar manager
   * @param view        the view for output
   * @param parser      the command parser
   * @param input       the Readable input source
   * @param interactive true for interactive mode (shows prompt), false for headless
   */
  public Controller(CalendarManager manager, ViewInterface view,
                    CommandParser parser, Readable input, boolean interactive) {
    this.manager = manager;
    this.view = view;
    this.parser = parser;
    this.input = input;
    this.interactive = interactive;
  }

  @Override
  public void run() throws IOException {
    boolean exitFound = false;
    try (Scanner scanner = new Scanner(input)) {
      if (interactive) {
        view.displayMessage("Enter commands (type 'exit' to quit):");
      }

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
          continue;
        }

        CommandInterface cmd = parser.parse(line);
        try {
          boolean shouldContinue = cmd.execute(manager, view);
          if (!shouldContinue) {
            // Exit command found
            exitFound = true;
            break;
          }
        } catch (InvalidCommandException e) {
          view.displayError(e.getMessage());
        } catch (Exception e) {
          view.displayError("Command failed: " + e.getMessage());
        }
      }

      // In headless mode, check if file ended without exit command
      if (!interactive && !exitFound) {
        view.displayError("Error: Commands file must end with 'exit' command");
      }
    }
  }
}