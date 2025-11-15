import calendar.command.CommandParser;
import calendar.controller.Controller;
import calendar.controller.ControllerInterface;
import calendar.model.CalendarManager;
import calendar.view.ConsoleView;
import calendar.view.ViewInterface;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Main entry point for the calendar application.
 *
 * <p>DESIGN CHANGE (HW5):
 * Updated to use CalendarManager instead of CalendarModel.
 * The application now supports multiple calendars with different timezones.
 */
public class CalendarRunner {
  /**
   * Main method to run the calendar application.
   *
   * @param args command line arguments: --mode [interactive|headless]
   *             [commands.txt]
   */
  public static void main(String[] args) {
    try {
      if (args.length < 2 || !args[0].equalsIgnoreCase("--mode")) {
        System.err.println("Usage: --mode [interactive|headless] [commands.txt]");
        System.exit(1);
      }
      CalendarManager manager = new CalendarManager();
      CommandParser parser = new CommandParser();
      ViewInterface view = new ConsoleView(System.out);

      String mode = args[1].toLowerCase();
      if (mode.equals("interactive")) {
        Readable input = new InputStreamReader(System.in);
        ControllerInterface controller = new Controller(manager, view, parser, input, true);
        controller.run();
      } else if (mode.equals("headless")) {
        if (args.length < 3) {
          System.err.println("Headless mode requires a commands file path");
          System.exit(1);
        }
        try (Reader reader = new FileReader(args[2])) {
          ControllerInterface controller = new Controller(manager, view, parser, reader, false);
          controller.run();
        }
      } else {
        System.err.println("Invalid mode: " + mode);
        System.exit(1);
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(2);
    }
  }
}
